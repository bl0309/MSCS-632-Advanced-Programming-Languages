"============================================================"
" Ride Sharing System - Smalltalk Implementation             "
" Demonstrates: Encapsulation, Inheritance, Polymorphism     "
"============================================================"

"============================================================"
" BASE CLASS: Ride                                           "
" Encapsulation via instance variables + accessor methods    "
"============================================================"
Object subclass: #Ride
    instanceVariableNames: 'rideID pickupLocation dropoffLocation distance fare'
    classVariableNames: ''
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!Ride class methodsFor: 'instance creation'!
id: anID pickup: pickup dropoff: dropoff distance: dist
    | ride |
    ride := self new.
    ride initID: anID pickup: pickup dropoff: dropoff distance: dist.
    ^ride
! !

!Ride methodsFor: 'initialization'!
initID: anID pickup: pickup dropoff: dropoff distance: dist
    rideID          := anID.
    pickupLocation  := pickup.
    dropoffLocation := dropoff.
    distance        := dist.
    fare            := self calculateFare.
! !

!Ride methodsFor: 'accessing'!
rideID          ^rideID!
pickupLocation  ^pickupLocation!
dropoffLocation ^dropoffLocation!
distance        ^distance!
fare            ^fare!
rideType        ^'Base Ride'! !

!Ride methodsFor: 'calculations'!
"Polymorphic - overridden in subclasses"
calculateFare
    ^0.0
! !

!Ride methodsFor: 'display'!
rideDetails
    Transcript
        show: '  Ride ID  : '; show: rideID printString; nl;
        show: '  Type     : '; show: self rideType; nl;
        show: '  Pickup   : '; show: pickupLocation; nl;
        show: '  Dropoff  : '; show: dropoffLocation; nl;
        show: '  Distance : '; show: distance printString; show: ' miles'; nl;
        show: '  Fare     : $'; show: (fare roundTo: 0.01) printString; nl.
! !


"============================================================"
" DERIVED CLASS: StandardRide                               "
" Demonstrates INHERITANCE from Ride                        "
"============================================================"
Ride subclass: #StandardRide
    instanceVariableNames: ''
    classVariableNames: 'BaseRate PerMileRate'
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!StandardRide class methodsFor: 'initialization'!
initialize
    BaseRate    := 1.50.
    PerMileRate := 1.10.
! !

!StandardRide methodsFor: 'accessing'!
rideType ^'Standard'! !

!StandardRide methodsFor: 'calculations'!
"POLYMORPHIC OVERRIDE - standard fare formula"
calculateFare
    ^1.50 + (distance * 1.10)
! !

StandardRide initialize.


"============================================================"
" DERIVED CLASS: PremiumRide                                "
" POLYMORPHISM: different fare calculation, extended display "
"============================================================"
Ride subclass: #PremiumRide
    instanceVariableNames: ''
    classVariableNames: 'BaseRate PerMileRate'
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!PremiumRide class methodsFor: 'initialization'!
initialize
    BaseRate    := 5.00.
    PerMileRate := 2.75.
! !

!PremiumRide methodsFor: 'accessing'!
rideType ^'Premium'! !

!PremiumRide methodsFor: 'calculations'!
"POLYMORPHIC OVERRIDE - premium fare formula"
calculateFare
    ^5.00 + (distance * 2.75)
! !

!PremiumRide methodsFor: 'display'!
"POLYMORPHIC OVERRIDE - extended details"
rideDetails
    super rideDetails.
    Transcript show: '  [Premium luxury vehicle included]'; nl.
! !

PremiumRide initialize.


"============================================================"
" DERIVED CLASS: SharedRide                                 "
" Third subclass with additional state (passengerCount)     "
"============================================================"
Ride subclass: #SharedRide
    instanceVariableNames: 'passengerCount'
    classVariableNames: ''
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!SharedRide class methodsFor: 'instance creation'!
id: anID pickup: pickup dropoff: dropoff distance: dist passengers: count
    | ride |
    ride := self new.
    ride setPassengers: count.
    ride initID: anID pickup: pickup dropoff: dropoff distance: dist.
    ^ride
! !

!SharedRide methodsFor: 'initialization'!
setPassengers: count
    passengerCount := count.
! !

!SharedRide methodsFor: 'accessing'!
rideType ^'Shared'!
passengerCount ^passengerCount! !

!SharedRide methodsFor: 'calculations'!
"POLYMORPHIC OVERRIDE - fare split among passengers"
calculateFare
    | total |
    total := 1.00 + (distance * 0.75).
    ^total / passengerCount
! !

!SharedRide methodsFor: 'display'!
rideDetails
    super rideDetails.
    Transcript
        show: '  Passengers: '; show: passengerCount printString;
        show: ' (fare split)'; nl.
! !


"============================================================"
" DRIVER CLASS                                              "
" ENCAPSULATION: assignedRides private, accessed via methods "
"============================================================"
Object subclass: #Driver
    instanceVariableNames: 'driverID name rating assignedRides'
    classVariableNames: ''
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!Driver class methodsFor: 'instance creation'!
id: anID name: aName rating: aRating
    | driver |
    driver := self new.
    driver initID: anID name: aName rating: aRating.
    ^driver
! !

!Driver methodsFor: 'initialization'!
initID: anID name: aName rating: aRating
    driverID     := anID.
    name         := aName.
    rating       := aRating.
    assignedRides := OrderedCollection new.
! !

!Driver methodsFor: 'accessing'!
driverID ^driverID!
name     ^name!
rating   ^rating!
rideCount ^assignedRides size!

"Encapsulated: no direct access to assignedRides collection"
totalEarnings
    | total |
    total := 0.0.
    assignedRides do: [:ride | total := total + ride fare].
    ^total
! !

!Driver methodsFor: 'operations'!
addRide: aRide
    assignedRides add: aRide.
! !

!Driver methodsFor: 'display'!
getDriverInfo
    Transcript
        nl;
        show: '=== Driver Info ==='; nl;
        show: '  ID      : '; show: driverID printString; nl;
        show: '  Name    : '; show: name; nl;
        show: '  Rating  : '; show: rating printString; show: ' / 5.0'; nl;
        show: '  Rides   : '; show: assignedRides size printString; nl;
        show: '  Earned  : $'; show: (self totalEarnings roundTo: 0.01) printString; nl;
        show: '  Ride IDs: '.
    assignedRides do: [:r | Transcript show: r rideID printString; show: ' '].
    Transcript nl.
! !


"============================================================"
" RIDER CLASS                                               "
" ENCAPSULATION: requestedRides private, accessed via methods"
"============================================================"
Object subclass: #Rider
    instanceVariableNames: 'riderID name requestedRides'
    classVariableNames: ''
    poolDictionaries: ''
    category: 'RideSharingSystem'!

!Rider class methodsFor: 'instance creation'!
id: anID name: aName
    | rider |
    rider := self new.
    rider initID: anID name: aName.
    ^rider
! !

!Rider methodsFor: 'initialization'!
initID: anID name: aName
    riderID        := anID.
    name           := aName.
    requestedRides := OrderedCollection new.
! !

!Rider methodsFor: 'operations'!
requestRide: aRide
    requestedRides add: aRide.
    Transcript
        show: '  Ride #'; show: aRide rideID printString;
        show: ' ('; show: aRide rideType; show: ') booked for '; show: name; show: '.'; nl.
! !

!Rider methodsFor: 'display'!
viewRides
    | total |
    total := 0.0.
    Transcript
        nl;
        show: '=== Ride History for '; show: name;
        show: ' (ID: '; show: riderID printString; show: ') ==='; nl.
    requestedRides isEmpty
        ifTrue: [Transcript show: '  No rides yet.'; nl]
        ifFalse: [
            requestedRides do: [:ride |
                ride rideDetails.
                total := total + ride fare.
                Transcript show: '  ---'; nl].
            Transcript
                show: '  Total Spent: $';
                show: (total roundTo: 0.01) printString; nl].
! !


"============================================================"
" MAIN PROGRAM - System Demonstration                       "
"============================================================"

| rides driver1 driver2 rider1 rider2 |

Transcript
    show: '========================================'; nl;
    show: '   RIDE SHARING SYSTEM - SMALLTALK'; nl;
    show: '========================================'; nl; nl.

"Create a collection of rides (polymorphic - mixed types)"
rides := OrderedCollection new.
rides add: (StandardRide id: 101 pickup: 'Downtown'       dropoff: 'Airport'             distance: 12.5).
rides add: (PremiumRide  id: 102 pickup: 'Hotel Grand'    dropoff: 'Convention Center'   distance: 3.2).
rides add: (SharedRide   id: 103 pickup: 'University'     dropoff: 'Mall'                distance: 7.8 passengers: 3).
rides add: (StandardRide id: 104 pickup: 'Suburbs'        dropoff: 'City Center'         distance: 18.0).
rides add: (PremiumRide  id: 105 pickup: 'Airport'        dropoff: 'Beachfront Resort'   distance: 22.0).

"--- Polymorphic demonstration ---"
Transcript show: '--- POLYMORPHIC FARE CALCULATION ---'; nl.
rides do: [:ride |
    Transcript show: '[Ride #'; show: ride rideID printString;
               show: ' | '; show: ride rideType; show: ']'; nl.
    ride rideDetails.   "calculateFare + rideDetails called polymorphically"
    Transcript nl].

"--- Create and populate Drivers ---"
driver1 := Driver id: 201 name: 'Alice Johnson' rating: 4.9.
driver2 := Driver id: 202 name: 'Bob Martinez'  rating: 4.6.

driver1 addRide: (rides at: 1).
driver1 addRide: (rides at: 3).
driver1 addRide: (rides at: 5).
driver2 addRide: (rides at: 2).
driver2 addRide: (rides at: 4).

driver1 getDriverInfo.
driver2 getDriverInfo.

"--- Create Riders and book rides ---"
rider1 := Rider id: 301 name: 'Carol White'.
rider2 := Rider id: 302 name: 'David Lee'.

Transcript nl; show: '--- RIDER BOOKING ---'; nl.
rider1 requestRide: (rides at: 1).
rider1 requestRide: (rides at: 2).
rider2 requestRide: (rides at: 3).
rider2 requestRide: (rides at: 4).
rider2 requestRide: (rides at: 5).

rider1 viewRides.
rider2 viewRides.

Transcript
    nl;
    show: '========================================'; nl;
    show: '        END OF DEMONSTRATION'; nl;
    show: '========================================'; nl.
