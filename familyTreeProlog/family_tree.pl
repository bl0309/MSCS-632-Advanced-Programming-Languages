% ============================================================
%  Family Tree Program in Prolog
%  Author: Bikram Lamsal
%  Description: Defines family facts and derived relationship
%               rules including grandparent, sibling, cousin,
%               and recursive descendant queries.
% ============================================================


% ------------------------------------------------------------
%  FACTS: Basic Relationships
% ------------------------------------------------------------

% parent(Parent, Child)
parent(george, john).
parent(george, susan).
parent(mary,   john).
parent(mary,   susan).

parent(john,   alice).
parent(john,   bob).
parent(linda,  alice).
parent(linda,  bob).

parent(susan,  carol).
parent(susan,  david).
parent(tom,    carol).
parent(tom,    david).

parent(alice,  emma).
parent(alice,  frank).
parent(henry,  emma).
parent(henry,  frank).

% male(Person)
male(george).
male(john).
male(bob).
male(tom).
male(david).
male(henry).
male(frank).

% female(Person)
female(mary).
female(susan).
female(alice).
female(linda).
female(carol).
female(emma).


% ------------------------------------------------------------
%  DERIVED RULES
% ------------------------------------------------------------

% father(X, Y) - X is the father of Y
father(X, Y) :-
    parent(X, Y),
    male(X).

% mother(X, Y) - X is the mother of Y
mother(X, Y) :-
    parent(X, Y),
    female(X).

% child(X, Y) - X is a child of Y
child(X, Y) :-
    parent(Y, X).

% ancestor_at_depth(X, Y, N) - X is an ancestor of Y N generations away
ancestor_at_depth(X, Y, 1) :-
    parent(X, Y).
ancestor_at_depth(X, Y, N) :-
    N > 1,
    parent(X, Z),
    N1 is N - 1,
    ancestor_at_depth(Z, Y, N1).

% grandparent(X, Y) - X is a grandparent of Y using recursion
grandparent(X, Y) :-
    ancestor_at_depth(X, Y, 2).

% sibling(X, Y) - X and Y share at least one parent and are not the same person
sibling(X, Y) :-
    X \= Y,
    setof(P, (parent(P, X), parent(P, Y)), _).

% cousin(X, Y) - X and Y have parents who are siblings
cousin(X, Y) :-
    X \= Y,
    setof((PX, PY), (parent(PX, X), parent(PY, Y), sibling(PX, PY)), _).

% ancestor(X, Y) - X is an ancestor of Y (recursive)
ancestor(X, Y) :-
    parent(X, Y).
ancestor(X, Y) :-
    parent(X, Z),
    ancestor(Z, Y).

% descendant(X, Y) - X is a descendant of Y (recursive)
descendant(X, Y) :-
    ancestor(Y, X).


% ------------------------------------------------------------
%  CONVENIENCE QUERY HELPERS
% ------------------------------------------------------------

% children_of(Parent, Child) - find all children of a given parent
children_of(Parent, Child) :-
    parent(Parent, Child).

% siblings_of(Person, Sibling) - find all siblings of a given person
siblings_of(Person, Sibling) :-
    sibling(Person, Sibling).

% grandchildren_of(Grandparent, Grandchild)
grandchildren_of(GP, GC) :-
    grandparent(GP, GC).

% is_cousin(X, Y) - succeeds if X and Y are cousins
is_cousin(X, Y) :-
    cousin(X, Y).

% ------------------------------------------------------------
%  END OF FILE
% ------------------------------------------------------------