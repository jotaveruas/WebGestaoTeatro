package web.gestaoteatro.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import web.gestaoteatro.core.validation.validator.StrongPasswordValidator;

/**
 * Valida a senha em texto recebida por um formulario ou DTO.
 * Combine com NotBlank quando o preenchimento for obrigatorio.
 * Nao aplicar ao atributo Usuario.senha que armazenara o hash.
 */
@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StrongPassword.List.class)
public @interface StrongPassword {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int minSize() default 8;
    int maxSize() default 32;
    int letters() default 0;
    int digits() default 0;
    int special() default 0;
    int uppercase() default 0;
    int lowercase() default 0;
    int sequences() default 0;
    int keyboardSequences() default 0;
    boolean checkForbidden() default true;
    String[] additionalForbidden() default {};

    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
            ElementType.ANNOTATION_TYPE, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        StrongPassword[] value();
    }
}
