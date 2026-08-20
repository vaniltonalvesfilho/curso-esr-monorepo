package com.algaworks.algafoodapi.infrastructure.service.email;

import com.algaworks.algafoodapi.domain.service.EnvioEmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Component
public class ProcessadorEmailTemplate {

    @Autowired
    private Configuration freemarkerConfig;

    public String processarTemplate(EnvioEmailService.Mensagem mensagem) {

        try {

            Template template = freemarkerConfig.getTemplate(mensagem.getCorpo());

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, mensagem.getVariaveis());

        } catch (Exception e) {

            throw new EmailException("Não foi possível montar o template do email", e);

        }
    }
}
