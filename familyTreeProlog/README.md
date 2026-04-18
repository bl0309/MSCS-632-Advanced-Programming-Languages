# Family Tree Program in Prolog

This project is a simple Prolog knowledge base that models family relationships.
It defines basic family facts and derives additional relationships using Prolog rules,
including `father`, `mother`, `child`, `grandparent`, `sibling`, `cousin`,
`ancestor`, and `descendant`.

## File

- `family_tree.pl` - contains all family facts and relationship rules

## Features

- Basic facts using `parent/2`, `male/1`, and `female/1`
- Derived relationships such as `father/2`, `mother/2`, and `child/2`
- Recursive relationship rules for ancestry and descendants
- Query helpers for children, siblings, grandchildren, and cousins

## Requirements

- SWI-Prolog

## How to Run

Open a terminal in this project folder and start SWI-Prolog:

```bash
swipl
```

Then load the Prolog file:

```prolog
['family_tree.pl'].
```

You can also load the file directly from the terminal:

```bash
swipl -s family_tree.pl
```

## Sample Queries

Find the children of `john`:

```prolog
children_of(john, X).
```

Find the siblings of `alice`:

```prolog
siblings_of(alice, X).
```

Check whether `alice` and `carol` are cousins:

```prolog
is_cousin(alice, carol).
```

Find all descendants of `george`:

```prolog
descendant(X, george).
```

Check whether `george` is a grandparent of `alice`:

```prolog
grandparent(george, alice).
```

## Main Predicates

- `parent(Parent, Child)`
- `male(Person)`
- `female(Person)`
- `father(X, Y)`
- `mother(X, Y)`
- `child(X, Y)`
- `grandparent(X, Y)`
- `sibling(X, Y)`
- `cousin(X, Y)`
- `ancestor(X, Y)`
- `descendant(X, Y)`
- `children_of(Parent, Child)`
- `siblings_of(Person, Sibling)`
- `grandchildren_of(Grandparent, Grandchild)`
- `is_cousin(X, Y)`

## Notes

- End each Prolog query with a period (`.`).
- If Prolog returns a result and you want additional answers, type `;`.
- Type `halt.` to exit SWI-Prolog.
