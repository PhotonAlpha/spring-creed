# Java有限状态机FSM
在java后端项目的开发过程中，经常会碰到需要进行状态转换的场景，比如订单的状态、任务的状态等等，通常会使用枚举对状态进行定义，并在需要时更改其枚举值。

但是在一些比较复杂的场景下，例如**状态数大于等于5个**，或者**大状态拥有子状态的场景**（如订单【交易中】的状态包含【待发货】【已发货】等子状态），再直接通过修改枚举值的方式已经难以进行维护，且会导致状态变更的代码在项目中散布，不仅在实现上不够优雅，而且在需要添加或者修改状态时牵一发而动全身。因此，我们需要有一种技术手段，来对状态进行统一管理，以及收束变更状态的代码入口。

# 有限状态机（Finite State Machine, FSM）就是用来干这个事的！
## 什么是有限状态机
> A **Finite State Machine** is a model of computation based on a hypothetical machine made of one or more states. Only one single state of this machine can be active at the same time. It means the machine has to transition from one state to another in to perform different actions.

状态机就是包含多个状态的数学模型，并可以在状态之间进行变换并且触发一些动作。

一个状态机一般包含以下几个元素
1. `State` 当前状态
2. `Event` 触发事件
3. `Transition` 状态变换，或者说下一个状态(次态)
4. `Action` 要执行的动作

> 在一些文章中，会将触发事件和执行动作的名称互换，这里笔者采用开源状态机 `Squirrel` 的定义

即在一个状态下，某些事件触发后变换成了另一个状态，并执行了一些操作

## 如何实现一个简单的状态机

这里根据多篇参考文档，总结以下四种实现方式并进行简述

### 1. `switch...case`
- 保存当前状态
- 状态变更时传入变更事件event, 先通过`if...else` 判断是哪个事件，再对当前事件 `event` 进行 `switch...case` 确定要执行的操作
- 进入下一个状态，并执行 `action`

简单代码如下
```java
public class StateMachine {
    // 当前状态
    private State currState;
    
    public StateMachine(State initState) {
        this.currState = initState;
    }
    
    public void transist(Event event) {
        if (当前是状态1) {
            switch (event) {
                case 事件1:
                    // 设置下一个状态
                    // 执行action
                case 事件2:
                  ...
            }
        }
        
        // ......
    }
}
```

该种方法实现是最简单的实现方法，在状态数量较少时，这是一个很高效的实现方案。但是在较复杂的状态下，会导致嵌套大量的 if...else 和 switch...case 语句，维护起来费劲而且实现不够优雅。

### 2. 状态模式 (State Design Pattern)
原始定义如下
> Allow an object to alter its behavior when its internal state changes.The object will appear to change its class.

> 外部的调用不用知道其内部如何实现状态和行为变化的

状态模式主要就是对转换规则进行封装，封装状态而暴露行为，状态的改变看起来就是行为发生改变，总结起来，状态模式干了这么几件事

- 需要定义状态抽象类 `AbstractState`，其中需要包含上下文 `Context`, 以及所有的抽象事件（`event`）对象的方法
```java
public abstract class AbstractState {
    // 上下文信息
    protected Context context;
    public void setContext(Context context) {
        this.context = context;
    }
    
    // 事件操作放在具体的状态定义内
    abstract void event1();
    abstract void event2();
    // ......
}
```

- 具体的状态需要继承并实现抽象状态
```java
public class State1 extends AbstractState {
    @Override
    void event1() {
        // set next state
        // do something else
    }
    // ......
}
```
- 上下文 Context 中需要包含所有所需信息，包括所有的状态实例，并指定一个属性指示当前是哪个状态实例
```java
public class Context {
    // 当前状态实例
    protected AbstractState currState;
    protected static final State1 state1 = new State1();
    protected static final State2 state2 = new State2();
    // ......
    public Context(AbstractState state) {
        setState(state);
    }
    // 具体的事件调用当前状态进行执行
    public void event1(){
        this.currState.event1();
    }
    // ......
}
```

由上可见，状态模式将与行为与状态相绑定，避免了直接去写大量的 `if...else` 和 `switch...case` 编写时可以方便地增加新的状态，并且只需要改变对象的状态就可以改变对象的行为，

### 3. Java枚举实现
利用枚举来实现，其主要思想在于可以将状态 `State` 和 事件 `Event` 都定义成一个枚举类型，并利用枚举也可以定义抽象方法的特性，使得其定义和动作可以写在一起

简单来说，其定义如下

- 定义状态枚举
```java
public enum StateEnum {
    State1 {
        @Override
        public void doEvent1(StateMachine stateMachine) {
            // set next state
            // do something else
        }
        @Override
        public void doEvent2(StateMachine stateMachine) {
            // set next state
            // do something else
        }
        // ......
    },
    // .......
    ;
    public abstract void doEvent1(StateMachine stateMachine);
    public abstract void doEvent2(StateMachine stateMachine);
    // ......
}
```
- 定义事件枚举
```java
public enum EventEnum {
    Event1 {
        @Override
        public void trigger(StateMachine stateMachine, StateEnum state) {
            return state.doEvent1(stateMachine);
        }
    },
    // ......
    ;
    public abstract void trigger(StateMachine stateMachine, StateEnum state);
}
```
- 组装状态机
```java
public class StateMachine {
    private StateEnum state;
    public StateMachine(StateEnum state) {
        this.state = state;
    }
    public void execute(EventEnum event) {
        event.trigger(this, this.state);
    }
```

使用枚举来定义可以使得我们的代码更加清晰

### 4. 保存转换映射
一言以蔽之，就是将状态机每一个状态转换所需要的几个要素都保存下来，在触发对应的事件时，遍历所有的状态映射记录，并根据状态以及上下文信息找到对应的记录，并执行转换得到次态

状态机的转换记录定义如下
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateMachineRecord {
    // 当前状态
    private State currentState;
    // 触发事件
    private Event event;
    // 次态
    private State nextState;
    // 执行动作
    private Action action;
}
```
这种方式实际上就是很多开源状态机的基础实现方式，不过在其实现中，会通过自定义注解的形式来使得代码更加简洁明了和优雅，并进行其他一些优化来优化性能，如记录索引以及懒加载等。

# Spring状态机原理，实现订单与物流解耦 
[传送门](https://docs.spring.io/spring-statemachine/docs/current/reference/#modules)

1. 添加依赖
`implementation 'org.springframework.statemachine:spring-statemachine-starter:3.2.0'`
2. 创建订单实体Order类
```java
@Data
@ToString
public class Order {
    private int id;
    private OrderStatus status;
}
```
3. 创建订单状态枚举和状态转换枚举类
```java
/**
 * 订单状态
 */
public enum OrderStatus {
    //待支付，待发货，待收货，订单结束
    WAIT_PAYMENT, WAIT_DELIVER, WAIT_RECEIVE, FINISH;
}
/**
 * 订单状态改变事件
 */
public enum OrderStatusChangeEvent {
    //支付，发货，确认收货
    PAYED, DELIVERY, RECEIVED;
}
```
4. 添加状态流转配置
```java
@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderStatusChangeEvent> {
    /**
     * 配置状态
     * @param states the {@link StateMachineStateConfigurer}
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderStatusChangeEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.WAIT_PAYMENT)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * 配置状态转换事件关系
     * @param transitions the {@link StateMachineTransitionConfigurer}
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER)
                .event(OrderStatusChangeEvent.PAYED)
                .and()
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE)
                .event(OrderStatusChangeEvent.DELIVERY)
                .and()
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH)
                .event(OrderStatusChangeEvent.RECEIVED);
    }
    
    @Bean
    public DefaultStateMachinePersister persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<Object, Object, Order>() {
            @Override
            public void write(StateMachineContext<Object, Object> context, Order contextObj) throws Exception {
                //此处并没有进行持久化操作
                System.out.println("DefaultStateMachinePersister write: " + context + " Order: " + contextObj);
            }

            @Override
            public StateMachineContext<Object, Object> read(Order contextObj) throws Exception {
                System.out.println("persister read:" + contextObj);
                //此处直接获取Order中的状态，其实并没有进行持久化读取操作
                return new DefaultStateMachineContext<>(contextObj.getStatus(), null, null, null);
            }
        });
    }
}
```
5. 添加订单状态监听器
```java
@Component("orderStateListener")
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListenerImpl {
    private static final Logger log = LoggerFactory.getLogger(OrderStateListenerImpl.class);

    @OnTransition(source = "WAIT_PAYMENT", target = "WAIT_DELIVER")
    public boolean payTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.WAIT_DELIVER);
        System.out.println("支付，状态机反馈信息：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public boolean deliverTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.WAIT_RECEIVE);
        System.out.println("发货，状态机反馈信息：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public boolean receiveTransition(Message<OrderStatusChangeEvent> message){
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.FINISH);
        System.out.println("收货，状态机反馈信息：" + message.getHeaders().toString());
        return true;
    }
}
```
6. 创建IOrderService接口,在service业务逻辑中应用
```java
public interface IOrderService {
    //创建新订单
    Order create();
    //发起支付
    Order pay(int id);
    //订单发货
    Order deliver(int id);
    //订单收货
    Order receive(int id);
    //获取所有订单信息
    Map<Integer, Order> getOrders();
}

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine;

    @Autowired
    private StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> persister;

    private int id = 1;
    private Map<Integer, Order> orders = new HashMap<>();

    @Override
    public Order create() {
        Order order = new Order();
        order.setStatus(OrderStatus.WAIT_PAYMENT);
        order.setId(id++);
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Order pay(int id) {
        Order order = orders.get(id);
        System.out.println("线程名称：" + Thread.currentThread().getName() + " 尝试支付，订单号：" + id);
        Message message = MessageBuilder.withPayload(OrderStatusChangeEvent.PAYED).
                setHeader("order", order).build();
        if (!sendEvent(message, order)) {
            System.out.println("线程名称：" + Thread.currentThread().getName() + " 支付失败, 状态异常，订单号：" + id);
        }
        return orders.get(id);
    }

    @Override
    public Order deliver(int id) {
        Order order = orders.get(id);
        System.out.println("线程名称：" + Thread.currentThread().getName() + " 尝试发货，订单号：" + id);
        if (!sendEvent(MessageBuilder.withPayload(OrderStatusChangeEvent.DELIVERY)
                .setHeader("order", order).build(), orders.get(id))) {
            System.out.println("线程名称：" + Thread.currentThread().getName() + " 发货失败，状态异常，订单号：" + id);
        }
        return orders.get(id);
    }

    @Override
    public Order receive(int id) {
        Order order = orders.get(id);
        System.out.println("线程名称：" + Thread.currentThread().getName() + " 尝试收货，订单号：" + id);
        if (!sendEvent(MessageBuilder.withPayload(OrderStatusChangeEvent.RECEIVED)
                .setHeader("order", order).build(), orders.get(id))) {
            System.out.println("线程名称：" + Thread.currentThread().getName() + " 收货失败，状态异常，订单号：" + id);
        }
        return orders.get(id);
    }

    @Override
    public Map<Integer, Order> getOrders() {
        return orders;
    }

    /**
     * 发送订单状态转换事件
     *
     * @param message
     * @param order
     * @return
     */
    private synchronized boolean sendEvent(Message<OrderStatusChangeEvent> message, Order order) {
        boolean result = false;
        try {
            orderStateMachine.start();
            //尝试恢复状态机状态
            persister.restore(orderStateMachine, order);
            //添加延迟用于线程安全测试
            Thread.sleep(1000);
            result = orderStateMachine.sendEvent(message);
            //持久化状态机状态
            persister.persist(orderStateMachine, order);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            orderStateMachine.stop();
        }
        return result;
    }
}
```

7. 编写客户端测试代码
```java
@SpringBootTest(classes = APMApplication.class)
public class APMApplicationTest {
    @Autowired
    private IOrderService orderService;

    @Test
    void testStateMachine() {
        orderService.create();
        orderService.create();
        orderService.pay(1);

        new Thread("client-thread"){
            @Override
            public void run() {
                orderService.deliver(1);
                orderService.receive(1);
            }
        }.start();

        orderService.pay(2);
        orderService.deliver(2);
        orderService.receive(2);
        System.out.println("全部订单状态：" + orderService.getOrders());
    }
}
```