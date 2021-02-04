# KryptPad
A cryptographic text editor with highly customizable encryption features, written in Java. It provides symmetric and asymmetric encryption & decryption with different cipher modes, paddings, and password-based encryption (custom password chosen by user). Supports key lengths up to 4096 bits.
The editor saves the encrypted text and the keys in seperate files, so the user has full control over the keys and can decide where to store them.

The strictly object-oriented source code has a multilayered architecture and implements the mediator pattern. `TextHandler` is the mediator between presentation layer (`Interface`), business logic (`Encryption`) and data layer (`Storage`). It is called by the `Interface` for saving and loading and takes appropriate action, like asking `Encryption` to encrypt the text and then handing it over to `Storage` for persisting it.

For more details, see the comments in the code, it's quite well documented.

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

