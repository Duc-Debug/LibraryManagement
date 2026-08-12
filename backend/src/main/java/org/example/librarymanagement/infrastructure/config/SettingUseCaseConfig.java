package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.setting.ManageSystemSettingService;
import org.example.librarymanagement.infrastructure.transaction.setting.TransactionalManageSystemSettingUseCase;
import org.example.librarymanagement.port.inbound.setting.ManageSystemSettingUseCase;
import org.example.librarymanagement.port.outbound.setting.SaveSystemSettingPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SettingUseCaseConfig {

    @Bean
    public ManageSystemSettingUseCase manageSystemSettingUseCase(
            SaveSystemSettingPort saveSystemSettingPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        ManageSystemSettingService service = new ManageSystemSettingService(
                saveSystemSettingPort,
                getAuthenticatedUserPort
        );
        return new TransactionalManageSystemSettingUseCase(service);
    }
}
