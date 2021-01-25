# KryptPad
A cryptographic text editor with highly customizable encryption features, written in Java. Supports key lengths up to 4096 bits.

The strictly object-oriented source code has a multilayered architecture and implements the mediator pattern. `TextHandler.java` is the mediator between presentation layer (Interface), business logic (Encryption) and data layer (Storage). It is called by the Interface for saving and loading and takes appropriate action, like asking Encryption to encrypt the text and then handing it over to Storage for persisting it.

Supported encryption algorithms:
- AES
- DES
- RSA
- ARC4

Supported block/stream cipher modes:
- ECB
- CBC
- CTR
- OFB
- CTS
- GCM
- None

