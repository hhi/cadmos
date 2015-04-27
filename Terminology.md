![http://www4.in.tum.de/~schwitze/cadmos/cadmos-sketch.png](http://www4.in.tum.de/~schwitze/cadmos/cadmos-sketch.png)

# Concepts and Terminology #

**Authors:** W. Schwitzer, V. Popa

This page defines central terms of Cadmos and explains the main concepts. The target audience are users as well as developers.

## Component ##
A component defines a function that maps _messages_ received on _incoming channels_ to messages sent on _outgoing channels_.
The function that is realized by a component is also called the _behavior_ of the component.
The behavior of a component is either defined directly by a _non-deterministic transition machine_ or it is defined by the composed behavior of several _children_ components.
This hierarchical parent-children relation describes the _structure_ of a system.
Additionally, a component has a number of _inbound_ and _outbound_ _ports_, which serve as _typed_ connection points for _incoming_ and _outgoing_ _channels_.
Together, the _inbound_ and _outbound_ _ports_ of a component define the _syntactic interface_ of that component.

See also:**[Channel](Terminology#Channel.md),
[Port](Terminology#Port.md),
[Message](Terminology#Message.md),
[Type](Terminology#Type.md),
[Structure](Terminology#Structure.md),
[Behavior](Terminology#Behavior.md),
[Machine](Terminology#Machine.md)**

## Channel ##
A channel transmits a _stream_ of _typed messages_ from a source component to a destination component.
We say, a channel is _outgoing_ from its source component and _incoming_ to its destination component.
A channel is not directly connected to _components_, rather it is connected to the _inbound_ and _outbound_ _ports_ of _components_.
Furthermore, a channel can _defer_ the transmission of messages by a certain _delay_, so a message sent by the source component at step _i_ will be received by the destination component at step _i + delay_.
A channel with a given _delay_ of _d_ also requires _d_ initial messages.

See also:**[Component](Terminology#Component.md),
[Port](Terminology#Port.md),
[Message](Terminology#Message.md),
[Type](Terminology#Type.md),
[Stream](Terminology#Stream.md),
[System Boundary](Terminology#System_Boundary.md),
[Rate](Terminology#Rate.md)**

## Port ##
A port belongs to a _component_ and represents a _typed_ source- or destination-connection point of _channels_.
Each port can have an _incoming_ _channel_ and a number of _outgoing_ _channels_.
The port itself can either be _inbound_ or _outbound_ with respect to its owning _component_.
If a port is _inbound_, it routes messages from the outside of its owning _component_ to the inside.
If a port is _outbound_, it routes messages from the inside of its owning _component_ to the outside.

See also:**[Component](Terminology#Component.md),
[Channel](Terminology#Channel.md),
[Type](Terminology#Type.md)**

## Message ##
A message has a data _type_ and is transmitted over a _channel_.
The _source port_ and _destination port_ of a _channel_ have data _types_ that are compatible with the data _type_ of transmitted messages.
A message is produced and sent by the _source component_ of a channel and received and consumed by the _destination component_ of a channel.
The transmission of messages can be _delayed_ by channels.
Additionally, messages may be produced and consumed at different _rates_.

See also:**[Component](Terminology#Component.md),
[Channel](Terminology#Channel.md),
[Port](Terminology#Port.md),
[Type](Terminology#Type.md),
[Stream](Terminology#Stream.md),
[Rate](Terminology#Rate.md)**

## Type ##
A type basically defines a set of all possible values that instances of that type can take.
Cadmos uses typed _ports_ and typed _variables_.
We distinguish between _elementary types_ and _composite types_.
Elementary types are _Boolean_ = {**false**, **true**}, subsets of _Integer_ numbers defined by {_min_, _min_+1, ..., _max_-1, _max_}, ranges in _Real_ numbers defined by `[`_min_, ..., _max_`]` and _Enumeration_ types defined by {_lit_<sub>1</sub>, ..., _lit<sub>n</sub>_}.
Composite types are _Record_ types defined by the set of all tuples {((_type_<sub>1</sub>, _lit_<sub>1</sub>), ..., (_type<sub>n</sub>_, _lit<sub>n</sub>_))}.

See also:**[Port](Terminology#Port.md),
[Variable](Terminology#Variable.md)**

## Stream ##
A stream _s_ is a sequence of typed _messages_ denoted as _s_ = (_m_<sub>0</sub>, ..., _m<sub>n-1</sub>_).
We say that _s_ has a length of _n_, denoted as #_s_ = _n_.
If _n_ is a finite number, the stream _s_ is a _finite stream_.
In Cadmos, we often analyze _reactive_ systems and we use the concept of _infinite streams_ to express, that reactive systems are typically designed to run (potentially) for ever.
We access a message at a given index _i_ by _s_(_i_).
In addition, we allow _empty messages_ in streams, denoted by the symbol `<>`.
For example, _s_ = (_a_, `<>`, `<>`, _b_) has the properties #_s_ = 4, _s_(0) = _a_, _s_(3) = _b_ and _s_(1) = _s_(2) = `<>`.

## System Boundary ##

## Rate ##

## Variable ##

## Atomic Component Network ##