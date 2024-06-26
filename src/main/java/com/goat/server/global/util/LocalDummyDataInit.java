package com.goat.server.global.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개발 환경에서 더미 데이터를 넣을 때 사용하는 annotation Profile이 dev인 경우 사용 일단 현재 개발단계여서 그냥 더미 데이터로 쓰려고 다 열어 놓음.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("!test")
@Transactional
@Component
public @interface LocalDummyDataInit {
}
