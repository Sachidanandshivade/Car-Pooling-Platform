import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllRides, startRide, completeRide, cancelRide } from "../services/rideService";

function parseJwt(token) {
    try {
        return JSON.parse(atob(token.split(".")[1]));
    } catch {
        return null;
    }
}

export default function MyRides() {
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(null);
    const [role, setRole] = useState("");
    const [userEmail, setUserEmail] = useState("");

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            const payload = parseJwt(token);
            if (payload) {
                setRole(payload.role || "");
                setUserEmail(payload.sub || "");
            }
        }
        fetchAllRides();
    }, []);

    const fetchAllRides = async () => {
        setLoading(true);
        try {
            const res = await getAllRides();
            setRides(res.data.data || []);
        } catch {
            // ignore
        } finally {
            setLoading(false);
        }
    };

    const myRides = rides.filter((ride) => {
        if (role === "DRIVER") return ride.driver?.email === userEmail;
        if (role === "PASSENGER") return ride.passenger?.email === userEmail;
        return false;
    });

    const handleAction = async (rideId, action) => {
        setActionLoading(`${rideId}-${action}`);
        try {
            let res;
            if (action === "start")    res = await startRide(rideId);
            if (action === "complete") res = await completeRide(rideId);
            if (action === "cancel")   res = await cancelRide(rideId);
            alert(res.data.message);
            fetchAllRides();
        } catch (err) {
            alert(err.response?.data?.message || "Action failed.");
        } finally {
            setActionLoading(null);
        }
    };

    const statusColor = (status) => {
        switch (status) {
            case "ACCEPTED":  return "bg-blue-100 text-blue-700";
            case "STARTED":   return "bg-yellow-100 text-yellow-700";
            case "COMPLETED": return "bg-green-100 text-green-700";
            case "CANCELLED": return "bg-red-100 text-red-700";
            default:          return "bg-gray-100 text-gray-600";
        }
    };

    const formatDate = (dt) => {
        if (!dt) return "—";
        return new Date(dt).toLocaleString("en-IN", {
            day: "2-digit", month: "short", year: "numeric",
            hour: "2-digit", minute: "2-digit",
        });
    };

    return (
        <div className="min-h-screen bg-gray-50">

            {/* Navbar */}
            <nav className="bg-black text-white px-6 py-4 flex items-center justify-between">
                <span className="text-xl font-bold">🚗 CarPooling</span>
                <Link to="/dashboard" className="text-sm text-gray-300 hover:text-white transition">
                    ← Back to Dashboard
                </Link>
            </nav>

            <div className="max-w-4xl mx-auto p-6 mt-6">
                <h1 className="text-2xl font-bold text-gray-800 mb-1">My Rides</h1>
                <p className="text-gray-500 text-sm mb-6">
                    {role === "DRIVER" ? "Rides you are driving." : "Your ride history as a passenger."}
                </p>

                {loading ? (
                    <div className="bg-white rounded-2xl p-10 text-center text-gray-400 shadow-sm border border-gray-100">
                        Loading your rides...
                    </div>
                ) : myRides.length === 0 ? (
                    <div className="bg-white rounded-2xl p-10 text-center text-gray-400 shadow-sm border border-gray-100">
                        No rides found yet.
                    </div>
                ) : (
                    <div className="space-y-4">
                        {myRides.map((ride) => (
                            <div
                                key={ride.id}
                                className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5"
                            >
                                {/* Ride Info + Actions */}
                                <div className="flex items-start justify-between gap-4 flex-wrap">

                                    {/* Ride Info */}
                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center gap-2 mb-2">
                                            <span className={`text-xs px-2 py-0.5 rounded-full font-semibold ${statusColor(ride.status)}`}>
                                                {ride.status}
                                            </span>
                                            <span className="text-xs text-gray-400">Ride #{ride.id}</span>
                                        </div>
                                        <p className="text-base font-semibold text-gray-800 mb-1">
                                            📍 {ride.source} → {ride.destination}
                                        </p>
                                        <div className="text-xs text-gray-500 space-y-0.5">
                                            <p>💰 Fare: ₹{ride.fare}</p>
                                            {ride.departureTime && <p>🕐 Departure: {formatDate(ride.departureTime)}</p>}
                                            {ride.startTime     && <p>▶️ Started:   {formatDate(ride.startTime)}</p>}
                                            {ride.endTime       && <p>✅ Ended:     {formatDate(ride.endTime)}</p>}
                                            {role === "DRIVER" && ride.passenger && (
                                                <p>👤 Passenger: {ride.passenger.name} ({ride.passenger.phone})</p>
                                            )}
                                            {role === "PASSENGER" && ride.driver && (
                                                <p>🧑‍✈️ Driver: {ride.driver.name} ({ride.driver.phone})</p>
                                            )}
                                        </div>
                                    </div>

                                    {/* Action Buttons — Driver only */}
                                    {role === "DRIVER" && (
                                        <div className="flex flex-col gap-2 min-w-[120px]">
                                            {ride.status === "ACCEPTED" && (
                                                <button
                                                    onClick={() => handleAction(ride.id, "start")}
                                                    disabled={actionLoading === `${ride.id}-start`}
                                                    className="bg-yellow-500 text-white text-sm px-4 py-2 rounded-lg hover:bg-yellow-600 transition disabled:opacity-50"
                                                >
                                                    {actionLoading === `${ride.id}-start` ? "..." : "▶ Start"}
                                                </button>
                                            )}
                                            {ride.status === "STARTED" && (
                                                <button
                                                    onClick={() => handleAction(ride.id, "complete")}
                                                    disabled={actionLoading === `${ride.id}-complete`}
                                                    className="bg-green-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-green-700 transition disabled:opacity-50"
                                                >
                                                    {actionLoading === `${ride.id}-complete` ? "..." : "✓ Complete"}
                                                </button>
                                            )}
                                            {(ride.status === "ACCEPTED" || ride.status === "STARTED") && (
                                                <button
                                                    onClick={() => handleAction(ride.id, "cancel")}
                                                    disabled={actionLoading === `${ride.id}-cancel`}
                                                    className="border border-red-300 text-red-600 text-sm px-4 py-2 rounded-lg hover:bg-red-50 transition disabled:opacity-50"
                                                >
                                                    {actionLoading === `${ride.id}-cancel` ? "..." : "✕ Cancel"}
                                                </button>
                                            )}
                                        </div>
                                    )}

                                    {/* Passenger cancel */}
                                    {role === "PASSENGER" && ride.status === "ACCEPTED" && (
                                        <button
                                            onClick={() => handleAction(ride.id, "cancel")}
                                            disabled={actionLoading === `${ride.id}-cancel`}
                                            className="border border-red-300 text-red-600 text-sm px-4 py-2 rounded-lg hover:bg-red-50 transition disabled:opacity-50"
                                        >
                                            {actionLoading === `${ride.id}-cancel` ? "..." : "✕ Cancel"}
                                        </button>
                                    )}

                                </div>

                                {/* Google Maps Route Button — shown for ACCEPTED and STARTED rides */}
                                {(ride.status === "ACCEPTED" || ride.status === "STARTED") && (
                                    <a
                                    href={`https://www.google.com/maps/dir/${encodeURIComponent(ride.source)}/${encodeURIComponent(ride.destination)}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="inline-flex items-center gap-2 bg-blue-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-blue-700 transition mt-3"
                                    >
                                    🗺️ View Route on Google Maps
                                    </a>
                                    )}

                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}