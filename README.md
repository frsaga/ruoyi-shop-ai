# RuoYi Shop with Business Data Assistant

A recovered Spring Boot/MyBatis shop backend with a Vue 3 frontend, including products, stores, carts, orders and an optional Qwen assistant. Upstream RuoYi code is retained with its MIT license; the framework is not claimed as a new original architecture.

## Configuration and recovery

Backend: RuoYi-Vue-master/RuoYi-Vue-master/. Frontend: RuoYi-Vue3-master/RuoYi-Vue3-master/. Supply your own DB_URL, DB_PASSWORD, JWT_SECRET, QWEN_API_KEY and optional DRUID_MONITOR_PASSWORD. Missing original Spring config was replaced with sanitized upstream 3.9.0 configuration. Use a fresh development database and initialize your own credentials; original SQL row data is excluded.

The local archive had over-redacted some normal password-field/runtime expressions. These were restored from matching upstream source or reconstructed as non-secret form expressions. No original secret was recovered. The password-cookie helper is disabled rather than distributing a client-side private key. This is recovered text source; missing binary assets, runtime resources and original Git history are not claimed as restored or fully verified.
