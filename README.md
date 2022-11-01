# flexibility-lab

Lab to implement flexibility quality attribute

# Prepare

- Run RabbitMQ
  ```
  docker run -d --name some-rabbit \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
  ```