package cz.karpi.iaea.questionnaire.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;

/**
 * Created by karpi on 2.5.17.
 */
@Component
public class FlowInterceptor extends HandlerInterceptorAdapter {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface FlowCheck {
        Flow.EFlowType[] value();
    }

    private final QuestionnaireFacadeService questionnaireFacadeService;

    @Autowired
    public FlowInterceptor(QuestionnaireFacadeService questionnaireFacadeService) {
        this.questionnaireFacadeService = questionnaireFacadeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final FlowCheck flowCheck = handlerMethod.getMethod().getAnnotation(FlowCheck.class);
        if (flowCheck == null || Stream.of(flowCheck.value()).noneMatch(v -> v.name().equals(questionnaireFacadeService.getCommonTo().getState()))) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
