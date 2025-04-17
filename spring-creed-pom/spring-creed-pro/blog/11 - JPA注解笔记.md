## Spring Boot与JPA整合 优化

1. cds
2. 

[参考文章](https://thorben-janssen.com/hibernate-features-with-spring-data-jpa/)

## 注解含义
- `@DynamicUpdate`

  - 对于更新，此实体是否应在使用动态sql生成时，仅在预处理sql语句中引用已更改的列。

  - 请注意，对于重新附加分离的实体，如果未启用select-before-update，则无法执行此操作。

    ```java
    Foo foo = new Foo(); 
    foo.setId(1); 
    foo.setName("贝多芬");
    这样会失效，需要先select
    ```

- `@SoftDelete`
  Soft delete with Hibernate >= 6.4被引入

  软删除注解: https://www.baeldung.com/java-hibernate-softdelete-annotation

  `@NotFound(action = NotFoundAction.IGNORE)` // 解决jpa cannot be mapped as LAZY as its associated entity is defined with @SoftDelete 问题
  https://stackoverflow.com/questions/77962258/hibernate-softdelete-problem-with-manytoonefetch-fetchtype-lazy

- `@Basic`

  `@Basic`表示一个简单的属性到数据库表的字段的映射,对于没有任何标注的 getXxxx() 方法,默认即为`@Basic(fetch=FetechType.EAGER)`
  `@Basic`参数：

    1. **fetch 表示该属性的加载读取策略**

       1.1 EAGER 主动抓取 （默认为EAGER）    

       1.2 LAZY 延迟加载,只有用到该属性时才会去加载

    2. **optional (默认为 true)**

       表示该属性是否允许为null

- `@Transient`

  JPA会忽略该属性，不会映射到数据库中，即程序运行后数据库中将不会有该字段

- `@Temporal`

  Java中没有定义 Date 类型的精度，而数据库中，表示时间类型的数据有 DATE,TIME,TIMESTAMP三种精度
    - @Temporal(TemporalType.DATE) 表示映射到数据库中的时间类型为 DATE，只有日期
    - @Temporal(TemporalType.TIME) 表示映射到数据库中的时间类型为 TIME，只有时间
    - @Temporal(TemporalType.TIMESTAMP) 表示映射到数据库中的时间类型为 TIMESTAMP,日期和时间都有

- `@Embedded` 和 `@Embeddable`

  用于一个实体类要在多个不同的实体类中进行使用，而本身又不需要独立生成一个数据库表

- `@MappedSuperclass`

  	It is used to group some common properties used by all entities, 
	
  	like `id`, or some auditing information like `createdAt` and `createdBy`.

- `@JoinColumn`

  定义表关联的外键字段名
  常用参数有：
    1. name: 指定映射到数据库中的外键的字段名
    2. unique: 是否唯一，默认为false
    3. nullable: 是否允许为null，默认为true
    4. insertable: 是否允许插入，默认为true
    5. updatetable: 是否允许更新，默认为true
    6. columnDefinition: 指定该属性映射到数据库中的实际类型，通常是自动判断。
    7. foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT)：指定外键相关信息，这里用法是指定外联关系但是不建立数据库外键

- `@ManyToOne、@OneToMany`

  参数：

  - targetEntity： 指定关联实体类型，默认为被注解的属性或方法所属的类

  - cascade： 级联操作策略

    - `CascadeType.ALL` **级联所有操作**

      Cascade all operations，清晰明确，拥有以上所有级联操作权限。
    
    - `CascadeType.PERSIST` **级联新增**

      经过实践检验，我的理解是：**给当前设置的实体操作另一个实体的权限**。这个理解可以推广到每一个CascadeType。因此，其余CascadeType枚举值将不再一一详细解释。

      可以看到，我们在上面的代码中给了Student对Course进行级联保存（cascade=CascadeType.PERSIST）的权限。此时，若Student实体持有的Course实体在数据库中不存在时，保存该Student时，系统将自动在Course实体对应的数据库中保存这条Course数据。而如果没有这个权限，则无法保存该Course数据。

    - `CascadeType.MERGE` **级联归并更新**

      Cascade merge operation，级联更新（合并）操作。
    
      当Student中的数据改变，会相应地更新Course中的数据。
    
    - `CascadeType.REMOVE` **级联删除**
    
      Cascade remove operation，级联删除操作。
    
      删除当前实体时，与它有映射关系的实体也会跟着被删除。
      
    - `CascadeType.REFRESH` **级联刷新**

      Cascade refresh operation，级联刷新操作。
      
      假设场景 有一个订单,订单里面关联了许多商品,这个订单可以被很多人操作,那么这个时候A对此订单和关联的商品进行了修改,与此同时,B也进行了相同的操作,但是B先一步比A保存了数据,那么当A保存数据的时候,就需要先刷新订单信息及关联的商品信息后,再将订单及商品保存。

    - `CascadeType.DETACH` **级联分离**

      Cascade detach operation，级联脱管/游离操作。
      
      如果你要删除一个实体，但是它有外键无法删除，你就需要这个级联权限了。它会撤销所有相关的外键关联。

    **一般情况下，不需要级联操作，手动操作即可。 即不要配置此选项。**[ref](https://stackoverflow.com/questions/29172313/spring-data-repository-does-not-delete-manytoone-entity?rq=4)
    
  - fetch： fetch 表示该属性的加载读取策略 (默认值为 EAGER)
  
    - EAGER 主动抓取
    - LAZY 延迟加载,只有用到该属性时才会去加载
  
  - optional： 默认为true，关联字段是否为空
  
    如果为false，则常与@JoinColumn一起使用
  
  - mappedBy： 指定关联关系，该参数只用于关联关系被拥有方
  
    只用于双向关联`@OneToOne`,`@OneToMany`,@ManyToMany。而`@ManyToOne`中没有
    `@OneToOne(mappedBy = “xxx”)`
  
    表示xxx所对应的类为关系被拥有方，而关联的另一方为关系拥有方
  
    - 关系拥有方：对应拥有外键的数据库表
    - 关系被拥有方：对应主键被子表引用为外键的数据库表
  
  - orphanRemoval:默认值为false
  
    判断是否自动删除与关系拥有方不存在联系的关系被拥有方(关系被拥有方的一个主键在关系拥有方中未被引用，
    当jpa执行更新操作时，是否删除数据库中此主键所对应的一条记录，若为true则删除)
  
- `@Enumerated`

  当实体类中有枚举类型的属性时，默认情况下自动生成的数据库表中对应的字段类型是枚举的索引值，是数字类型的，若希望数据库中存储的是枚举对应的String类型，在属性上加入`@Enumerated(EnumType.STRING)`注解即可。

  ```java
  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private RoleEnum role;
  ```

##### Entity 的回调事件注解

- `@PrePersist`

  EntityManager.persist 方法调用之前的回调注解，可以理解为新增之前的回调方法

- `@PostPersist`

  在操作 EntityManager.persist 方法之后调用的回调注解，EntityManager.flush 或 EntityManager.commit 方法之后调用此方法，也可以理解为在保存到数据库之后进行调用

- `@PreRemote`

  在操作 EntityManager.remote 之前调用的回调注解，可以理解为在操作删除方法之前调用

- `@PostRemote`

  在操作 EntityManager.remote 之后调用的回调注解，可以理解为在删除方法操作之后调用

- `@PreUpdate`

  在实体更新之前调用，所谓的更新其实是在 merge 之后，实体发生变化，这一注解可以理解为在变化储存到数据库之前调用

- `@PostUpdate`

  在实体更新之后调用，即实体的字段的值变化之后，在调用 EntityManager.flush 或 EntityManager.commit 方法之后调用这个方法。

- `@PostLoad`

  在实体从 DB 加载到程序里面之后回调

  

  关于上表所述的⼏个⽅法有⼀些需要注意的地⽅，如下：

  1. 回调函数都是和 EntityManager.flush 或 EntityManager.commit 在同⼀个线程⾥⾯执⾏的，只不过调⽤⽅法有先后之分，都是同步调⽤，所以当任何⼀个回调⽅法⾥⾯发⽣异常，都会触发事务进⾏回滚，⽽不会触发事务提交。
  2. Callbacks 注解可以放在实体⾥⾯，可以放在 super-class ⾥⾯，也可以定义在 entity 的 listener ⾥⾯，但需要注意的是：放在实体（或者 super-class）⾥⾯的⽅法，签名格式为void methodName()，即没有参数，⽅法⾥⾯操作的是 this 对象⾃⼰；放在实体的 EntityListener ⾥⾯的⽅法签名格式为 void methodName(Object)，也就是⽅法可以有参数，参数是代表⽤来接收回调⽅法的实体。
  3. 使上述注解⽣效的回调⽅法可以是 public、private、protected、friendly 类型的，但是不能是 static 和 finnal 类型的⽅法。





# EntityGraph实体图

解决N+1问题

https://juejin.cn/post/6869650227268157454



# JPA 使用思考

1. JPA的思想就是基于 @OneToMany @ManyToOne 
2. 如果在不使用的情况下，只能手动做value转换了