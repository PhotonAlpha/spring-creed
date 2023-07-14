# spring boot 自定义验证
## 实现方式一 ： `implements org.springframework.validation.Validator`
此实现方式可以用来验证请求体，当需要查询数据库验证数据正确性的时候，可以使用此方式

使用步骤：
1. `implements org.springframework.validation.Validator`,如 `DeptValidator`
2. 在controller中添加如下代码:
    ```java
    @Resource
    private DeptValidator deptValidator;
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (Optional.ofNullable(binder.getTarget()).map(Object::getClass).filter(deptValidator::supports).isPresent()) {
            binder.addValidators(deptValidator);
        }
    }
    ```
   每次request请求都会调用一次`@InitBinder`，启用验证器
3. 添加`@Validated` 在 `@RequestBody DeptCreateReqVO reqVO` 处

发起请求就会进入验证器

## 实现方式二 ： `implements jakarta.validation.ConstraintValidator<IdIdentifier, Object>`  
此实现方式可以用来验证请求体中的某一个字段或者请求参数，如`@PathVariable` `@RequestHeader` `请求体中的某个字段`，当需要查询数据库验证数据正确性的时候，可以使用此方式

1. 创建注解
    ```java
    @Target({METHOD, FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = {IdIdentifierValidator.class})
    public @interface IdIdentifier {
        String message() default "id not found ";
    
        Class<?>[] groups() default {};
    
        Class<? extends Payload>[] payload() default {};
    }
    ```
2. 实现 `implements ConstraintValidator<IdIdentifier, Object>`, 如 `IdIdentifierValidator.java`
3. 启用注解，在类上需要添加`@Validated`, 如果自定义`IdIdentifier`会用到分组的话，还需要将分组添加到类上。
    ```java
        @Validated({Default.class, ReferenceNumGroup.class})
        public class DeptController {}
    ```
   在类级别上使用 `@Validated` 注解时，并不要求使用自定义注解才会生效。`@Validated` 注解可用于**启用方法参数的验证**，而**无论参数是否使用了自定义注解，都将受到 `@Validated` 注解的影响。**。   

4. `@PathVariable @IdIdentifier Long id` 
添加注解启用验证器
5. 如果是验证 `@RequestBody DeptCreateReqVO reqVO`，则需要在使用`@RequestBody @Validated({Default.class, ReferenceNumGroup.class}) DeptCreateReqVO reqVO`，这样会验证requestBody中的属性。
