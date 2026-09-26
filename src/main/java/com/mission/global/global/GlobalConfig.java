package com.mission.global.global;

import com.mission.global.eventPublisher.EventPublisher;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {

    @Getter
    private static EventPublisher eventPublisher;

    @Autowired //static 필드에는 직접 주입이 안 되기 때문에 필요
    public void setEventPublisher(EventPublisher eventPublisher){GlobalConfig.eventPublisher = eventPublisher;}
}
