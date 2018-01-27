# ObjectProvider

![alt status](https://travis-ci.org/DSSB/objectprovider.svg?branch=master)

## NOTE: This README.md is a work in progress.

ObjectProvider is a super simple by powerful dependency injection utility.
It can be used in many different way and many level of coupling to the code.
Inspired by Guice, but ObjectProvider try its best to decoupling itself from the code that use it.

```
  +--------+                            +--------+
  | Client | ---> (ObjectProvider) ---> | Source |
  +--------+                            +--------+
```

## Level 0 - The source has no coupling to ObjectProvider
The source can have not mention, no dependency the ObjectProvider at all.
When the Client ask ObjectProvider for an object of Source,
  ObjectProvider will look for any clue to create the object.

## Level 1 - The source can have matched-name annotations
ObjectProvider looks for clues in the source.
The source can use annotations to add more clue.
The annotations, however, does not need to be the ones packaged with ObjectProvider.
ObjectProvider will look for annotation with matched-names instead of the actual class.
This means that the Source can define and use its own annotations
  as long as the simple name matched what ObjectProvider expect,
  ObjectProvider will use it as the clues.

## Level 2 - The source can ask for Object by only depends on ObjetProvider API.
To accomplished its work the Source might need to ask for some objects from ObjectProvider.
ObjectProvider is designed so that a piece of code can depends in its API

