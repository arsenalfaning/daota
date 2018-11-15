package org.psg.like.anno;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(DaoScanRegistar.class)
public @interface DaoScan {
    String[] basePackage() default {};
}
