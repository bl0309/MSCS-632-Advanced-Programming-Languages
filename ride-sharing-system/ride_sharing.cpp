/*
 * Ride Sharing System - C++ Implementation
 * Demonstrates: Encapsulation, Inheritance, Polymorphism
 */

#include <iostream>
#include <vector>
#include <string>
#include <memory>
#include <iomanip>
#include <numeric>

// ============================================================
// BASE CLASS: Ride
// Demonstrates ENCAPSULATION via private members + public API
// ============================================================
class Ride {
private:
    int rideID;
    std::string pickupLocation;
    std::string dropoffLocation;
    double distance; // miles

protected:
    double fare;

public:
    Ride(int id, const std::string& pickup, const std::string& dropoff, double dist)
        : rideID(id), pickupLocation(pickup), dropoffLocation(dropoff), distance(dist), fare(0.0) {}

    virtual ~Ride() = default;

    // Getters (encapsulation)
    int getRideID() const { return rideID; }
    std::string getPickup() const { return pickupLocation; }
    std::string getDropoff() const { return dropoffLocation; }
    double getDistance() const { return distance; }
    double getFare() const { return fare; }

    // POLYMORPHIC methods - overridden in subclasses
    virtual double calculateFare() = 0;
    virtual std::string getRideType() const = 0;

    virtual void rideDetails() const {
        std::cout << std::fixed << std::setprecision(2);
        std::cout << "  Ride ID     : " << rideID << "\n"
                  << "  Type        : " << getRideType() << "\n"
                  << "  Pickup      : " << pickupLocation << "\n"
                  << "  Dropoff     : " << dropoffLocation << "\n"
                  << "  Distance    : " << distance << " miles\n"
                  << "  Fare        : $" << fare << "\n";
    }
};

// ============================================================
// DERIVED CLASS: StandardRide
// Demonstrates INHERITANCE from Ride
// ============================================================
class StandardRide : public Ride {
public:
    static constexpr double BASE_RATE = 1.50;
    static constexpr double PER_MILE  = 1.10;

    StandardRide(int id, const std::string& pickup, const std::string& dropoff, double dist)
        : Ride(id, pickup, dropoff, dist) {
        fare = calculateFare();
    }

    // POLYMORPHIC override
    double calculateFare() override {
        return BASE_RATE + (getDistance() * PER_MILE);
    }

    std::string getRideType() const override { return "Standard"; }
};

// ============================================================
// DERIVED CLASS: PremiumRide
// Demonstrates INHERITANCE + POLYMORPHISM (different fare logic)
// ============================================================
class PremiumRide : public Ride {
public:
    static constexpr double BASE_RATE = 5.00;
    static constexpr double PER_MILE  = 2.75;

    PremiumRide(int id, const std::string& pickup, const std::string& dropoff, double dist)
        : Ride(id, pickup, dropoff, dist) {
        fare = calculateFare();
    }

    double calculateFare() override {
        return BASE_RATE + (getDistance() * PER_MILE);
    }

    std::string getRideType() const override { return "Premium"; }

    void rideDetails() const override {
        Ride::rideDetails();
        std::cout << "  [Premium luxury vehicle included]\n";
    }
};

// ============================================================
// DERIVED CLASS: SharedRide
// Third subclass for richer demonstration
// ============================================================
class SharedRide : public Ride {
private:
    int passengerCount;
public:
    static constexpr double BASE_RATE = 1.00;
    static constexpr double PER_MILE  = 0.75;

    SharedRide(int id, const std::string& pickup, const std::string& dropoff, double dist, int passengers = 2)
        : Ride(id, pickup, dropoff, dist), passengerCount(passengers) {
        fare = calculateFare();
    }

    double calculateFare() override {
        double total = BASE_RATE + (getDistance() * PER_MILE);
        return total / passengerCount; // Split among passengers
    }

    std::string getRideType() const override { return "Shared"; }

    void rideDetails() const override {
        Ride::rideDetails();
        std::cout << "  Passengers  : " << passengerCount << " (fare split)\n";
    }
};

// ============================================================
// DRIVER CLASS
// Demonstrates ENCAPSULATION: assignedRides is private
// ============================================================
class Driver {
private:
    int driverID;
    std::string name;
    double rating;
    std::vector<std::shared_ptr<Ride>> assignedRides; // private!

public:
    Driver(int id, const std::string& n, double r)
        : driverID(id), name(n), rating(r) {}

    void addRide(std::shared_ptr<Ride> ride) {
        assignedRides.push_back(ride);
    }

    double totalEarnings() const {
        double total = 0.0;
        for (const auto& r : assignedRides)
            total += r->getFare();
        return total;
    }

    int getRideCount() const { return (int)assignedRides.size(); }

    void getDriverInfo() const {
        std::cout << std::fixed << std::setprecision(2);
        std::cout << "\n=== Driver Info ===\n"
                  << "  ID       : " << driverID << "\n"
                  << "  Name     : " << name << "\n"
                  << "  Rating   : " << rating << " / 5.0\n"
                  << "  Rides    : " << assignedRides.size() << "\n"
                  << "  Earnings : $" << totalEarnings() << "\n";
        if (!assignedRides.empty()) {
            std::cout << "  Ride IDs : ";
            for (const auto& r : assignedRides)
                std::cout << r->getRideID() << " ";
            std::cout << "\n";
        }
    }
};

// ============================================================
// RIDER CLASS
// Demonstrates ENCAPSULATION: requestedRides is private
// ============================================================
class Rider {
private:
    int riderID;
    std::string name;
    std::vector<std::shared_ptr<Ride>> requestedRides; // private!

public:
    Rider(int id, const std::string& n) : riderID(id), name(n) {}

    void requestRide(std::shared_ptr<Ride> ride) {
        requestedRides.push_back(ride);
        std::cout << "  Ride #" << ride->getRideID()
                  << " (" << ride->getRideType() << ") booked for " << name << ".\n";
    }

    void viewRides() const {
        std::cout << std::fixed << std::setprecision(2);
        std::cout << "\n=== Ride History for " << name << " (ID: " << riderID << ") ===\n";
        if (requestedRides.empty()) {
            std::cout << "  No rides yet.\n";
            return;
        }
        double total = 0.0;
        for (const auto& r : requestedRides) {
            r->rideDetails();
            total += r->getFare();
            std::cout << "  ---\n";
        }
        std::cout << "  Total Spent : $" << total << "\n";
    }
};

// ============================================================
// MAIN: System demonstration
// ============================================================
int main() {
    std::cout << "========================================\n";
    std::cout << "     RIDE SHARING SYSTEM - C++\n";
    std::cout << "========================================\n\n";

    // Create rides using polymorphism (base class pointers)
    std::vector<std::shared_ptr<Ride>> rides;
    rides.push_back(std::make_shared<StandardRide>(101, "Downtown", "Airport", 12.5));
    rides.push_back(std::make_shared<PremiumRide> (102, "Hotel Grand", "Convention Center", 3.2));
    rides.push_back(std::make_shared<SharedRide>  (103, "University", "Mall", 7.8, 3));
    rides.push_back(std::make_shared<StandardRide>(104, "Suburbs", "City Center", 18.0));
    rides.push_back(std::make_shared<PremiumRide> (105, "Airport", "Beachfront Resort", 22.0));

    // --- Polymorphic demonstration ---
    std::cout << "--- POLYMORPHIC FARE CALCULATION ---\n";
    for (const auto& ride : rides) {
        // calculateFare() + rideDetails() called polymorphically
        std::cout << "[Ride #" << ride->getRideID() << " | "
                  << ride->getRideType() << "]\n";
        ride->rideDetails();
        std::cout << "\n";
    }

    // --- Create Drivers ---
    Driver driver1(201, "Alice Johnson", 4.9);
    Driver driver2(202, "Bob Martinez", 4.6);

    driver1.addRide(rides[0]);
    driver1.addRide(rides[2]);
    driver1.addRide(rides[4]);
    driver2.addRide(rides[1]);
    driver2.addRide(rides[3]);

    driver1.getDriverInfo();
    driver2.getDriverInfo();

    // --- Create Riders ---
    Rider rider1(301, "Carol White");
    Rider rider2(302, "David Lee");

    std::cout << "\n--- RIDER BOOKING ---\n";
    rider1.requestRide(rides[0]);
    rider1.requestRide(rides[1]);
    rider2.requestRide(rides[2]);
    rider2.requestRide(rides[3]);
    rider2.requestRide(rides[4]);

    rider1.viewRides();
    rider2.viewRides();

    std::cout << "\n========================================\n";
    std::cout << "         END OF DEMONSTRATION\n";
    std::cout << "========================================\n";
    return 0;
}
