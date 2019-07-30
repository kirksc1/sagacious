package com.github.kirksc1.sagacious.sample.orderservice;

import com.github.kirksc1.sagacious.SagaOrchestrated;
import com.github.kirksc1.sagacious.SagaManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

    @NonNull
    private final OrderAssembler assembler;

    @NonNull
    private final OrderService service;

    @NonNull
    private SagaManager sagaManager;

    @RequestMapping(path = "/orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @SagaOrchestrated
    public String createOrder(@RequestBody OrderResource orderResource) throws Exception {

        Order order = assembler.assembleOrder(orderResource);
        order = service.createOrder(order);

        return order.getGuid();
    }
}
