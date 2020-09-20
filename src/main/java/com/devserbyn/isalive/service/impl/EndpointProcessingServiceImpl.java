package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.UserSetupStepsBO;
import com.devserbyn.isalive.model.UserSetupStepsBO.UserSetupSteps;
import com.devserbyn.isalive.model.enums.EndpointCheckStatus;
import com.devserbyn.isalive.model.enums.TextResourceKeys;
import com.devserbyn.isalive.service.CheckEndpointService;
import com.devserbyn.isalive.service.EndpointProcessingService;
import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.service.UserService;
import com.devserbyn.isalive.utility.WebUtility;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devserbyn.isalive.constant.STR_CONSTANT.ENDPOINT_ISALIVE_FORMAT;
import static com.devserbyn.isalive.constant.STR_CONSTANT.ENDPOINT_ISALIVE_REQ_METHOD;
import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.ERROR;
import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.ERROR_WITH_INFO;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_DOWN;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_ERROR;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_ERROR_WITH_INFO;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_RESOLVED;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_TIMEOUT;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ENDPOINT_UP;

@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointProcessingServiceImpl implements EndpointProcessingService {

    private final CheckEndpointService checkEndpointService;
    private final TextResourceService textResourceService;
    private final UserService userService;
    private final UserSetupStepsBO userSetupStepsBO;

    @Override
    public String resolveEndpointSetup(Update update, UserSetupSteps userSetupSteps) {
        final String input = update.getMessage().getText();
        long chatID = update.getMessage().getChatId();
        if (userSetupSteps.isWaitingForAppURL()) {
            boolean isUnique = checkEndpointService.findAllByUser(userService.findByChatID(chatID).orElseThrow(IllegalStateException::new))
                                                   .stream().noneMatch(x -> x.getEndpointURL().equals(input));
            if (!isUnique) {
                return textResourceService.get(TextResourceKeys.URL_NOT_UNIQUE);
            }
            boolean isValid = WebUtility.validateURL(input);
            if (!isValid) {
                return textResourceService.get(TextResourceKeys.INVALID_URL);
            }

            CheckEndpoint endpoint = new CheckEndpoint();
            endpoint.setUser(userService.findByChatID(update.getMessage().getChatId()).orElseThrow(RuntimeException::new));
            endpoint.setEndpointURL(input);
            checkEndpointService.save(endpoint);

            userSetupSteps.setWaitingForAppURL(false);
            userSetupSteps.setWaitingForSupportsIsAlive(true);

            return textResourceService.get(TextResourceKeys.ASK_FOR_ISALIVE_URL_SUPPORT);
        } else if (userSetupSteps.isWaitingForSupportsIsAlive()) {
            String formattedInput = input.toLowerCase().trim();
            boolean isValid = formattedInput.equals("yes") || formattedInput.equals("no");
            if (!isValid) {
                return textResourceService.get(TextResourceKeys.YES_NO_INVALID_ANSWER);
            }
            boolean supportsIsAlive = formattedInput.equals("yes");

            CheckEndpoint endpoint = checkEndpointService.findLastAddedByUser(userService.findByChatID(chatID).orElseThrow(IllegalStateException::new));
            endpoint.setSupportsIsAlive(supportsIsAlive);

            EndpointCheckStatus endpointCheckStatus = this.checkEndpoint(endpoint);
            if (endpointCheckStatus != EndpointCheckStatus.OK) {
                userSetupStepsBO.removeByChatId(chatID);
                checkEndpointService.delete(endpoint);
                return getAnswerOnEndpointCheckStatus(endpointCheckStatus, endpoint);
            }

            endpoint.setLastCheckStatus(endpointCheckStatus);
            checkEndpointService.save(endpoint);

            userSetupSteps.setWaitingForSupportsIsAlive(false);
            userSetupStepsBO.removeByChatId(chatID);

            return textResourceService.get(TextResourceKeys.URL_SAVED_SUCCESS);
        } else {
            throw new IllegalStateException("Error while waiting for endpoint URL or supports IsAlive flag");
        }
    }

    @Override
    public EndpointCheckStatus checkEndpoint(CheckEndpoint endpoint) {
        try {
            if (endpoint.isSupportsIsAlive()) {
                JSONObject response = WebUtility.readJsonFromUrl(String.format(ENDPOINT_ISALIVE_FORMAT, endpoint.getEndpointURL()), ENDPOINT_ISALIVE_REQ_METHOD);
                if (response.getBoolean("status")) {
                    return EndpointCheckStatus.OK;
                } else {
                    String info = response.getString("info");
                    endpoint.setInfo(info);
                    return (info != null) ? ERROR_WITH_INFO : ERROR;
                }
            } else {
                URL url = new URL(endpoint.getEndpointURL());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3_000);
                int responseCodeRange = connection.getResponseCode() / 100;
                switch (responseCodeRange) {
                    case 1:
                    case 2: return EndpointCheckStatus.OK;
                    case 5: return EndpointCheckStatus.ERROR;
                    default: return EndpointCheckStatus.DOWN;
                }
            }
        }
        catch (SocketTimeoutException timeoutException) {
            return EndpointCheckStatus.TIMEOUT;
        }
        catch (Exception e) {
            log.trace(String.format("URL %s connection gave an exception", endpoint.getEndpointURL()), e);
            return EndpointCheckStatus.ERROR;
        }
    }

    @Override
    public String getAnswerOnEndpointCheckStatus(EndpointCheckStatus status, CheckEndpoint endpoint) {
        switch (status) {
            case OK: return String.format(textResourceService.get(ENDPOINT_UP), endpoint.getEndpointURL());
            case RESOLVED: return String.format(textResourceService.get(ENDPOINT_RESOLVED), endpoint.getEndpointURL());
            case ERROR: return String.format(textResourceService.get(ENDPOINT_ERROR), endpoint.getEndpointURL());
            case ERROR_WITH_INFO: return String.format(textResourceService.get(ENDPOINT_ERROR_WITH_INFO), endpoint.getEndpointURL(), endpoint.getInfo());
            case DOWN: return String.format(textResourceService.get(ENDPOINT_DOWN), endpoint.getEndpointURL());
            case TIMEOUT: return String.format(textResourceService.get(ENDPOINT_TIMEOUT), endpoint.getEndpointURL());
            default: throw new IllegalArgumentException();
        }
    }

}
