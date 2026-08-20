package com.algaworks.algafoodapi.core.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.algaworks.algafoodapi.core.email.EmailProperties.Implementacao;
import com.algaworks.algafoodapi.domain.service.EnvioEmailService;
import com.algaworks.algafoodapi.infrastructure.service.email.FakeEnvioEmailService;
import com.algaworks.algafoodapi.infrastructure.service.email.SandboxEnvioEmailService;
import com.algaworks.algafoodapi.infrastructure.service.email.SmtpEnvioEmailService;

@Configuration
public class EmailConfig {
	
	@Autowired
	private EmailProperties emailProperties;
	
	@Bean
	public EnvioEmailService envioEmailService() {
		
		if (emailProperties.getImpl().equals(Implementacao.FAKE)) {
			return new FakeEnvioEmailService();
		} else if (emailProperties.getImpl().equals(Implementacao.SMTP)) {
			return new SmtpEnvioEmailService();
		} else {
			return new SandboxEnvioEmailService();
		}
		
	}
	
}
