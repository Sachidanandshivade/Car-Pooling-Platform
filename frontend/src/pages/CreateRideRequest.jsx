import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { createRideRequest } from "../services/rideRequestService";

export default function CreateRideRequest() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        source: "",
        destination: "",
        requestTime: "",
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    // 🚀 NEW STATES (FARE FEATURE)
    const [fare, setFare] = useState(null);
    const [fareLoading, setFareLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 🚀 FETCH FARE FROM BACKEND
    const fetchFare = async (source, destination) => {
        if (!source || !destination) {
            setFare(null);
            return;
        }

        setFareLoading(true);
        try {
            const res = await fetch(
                `https://car-pooling-platform-3.onrender.com/requests/estimate-fare?source=${source}&destination=${destination}`
            );
            const data = await res.json();
            setFare(data.data);
        } catch (err) {
            console.log("Fare error:", err);
            setFare(null);
        } finally {
            setFareLoading(false);
        }
    };

    // 🚀 AUTO CALL FARE WHEN USER TYPES (DEBOUNCE)
    useEffect(() => {
        const timer = setTimeout(() => {
            fetchFare(formData.source, formData.destination);
        }, 500);

        return () => clearTimeout(timer);
    }, [formData.source, formData.destination]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const payload = {
                ...formData,
                requestTime: new Date(formData.requestTime)
                    .toISOString()
                    .slice(0, 19),
            };

            await createRideRequest(payload);

            alert("Ride request created successfully!");
            navigate("/dashboard");
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create ride request.");
        } finally {
            setLoading(false);
        }
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

            <div className="max-w-lg mx-auto p-6 mt-6">

                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">

                    <h1 className="text-2xl font-bold text-gray-800 mb-1">
                        Request a Ride
                    </h1>

                    <p className="text-gray-500 text-sm mb-6">
                        Fill in details and see fare before booking.
                    </p>

                    {error && (
                        <div className="bg-red-50 border border-red-300 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>

                        {/* SOURCE */}
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Pickup Location
                            </label>
                            <input
                                type="text"
                                name="source"
                                placeholder="e.g. Koramangala, Bangalore"
                                value={formData.source}
                                onChange={handleChange}
                                required
                                className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black text-sm"
                            />
                        </div>

                        {/* DESTINATION */}
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Drop Location
                            </label>
                            <input
                                type="text"
                                name="destination"
                                placeholder="e.g. Electronic City, Bangalore"
                                value={formData.destination}
                                onChange={handleChange}
                                required
                                className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black text-sm"
                            />
                        </div>

                        {/* DATE TIME */}
                        <div className="mb-6">
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Pickup Date & Time
                            </label>
                            <input
                                type="datetime-local"
                                name="requestTime"
                                value={formData.requestTime}
                                onChange={handleChange}
                                required
                                className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black text-sm"
                            />
                        </div>

                        {/* 🚀 FARE DISPLAY */}
                        {fareLoading && (
                            <p className="text-sm text-gray-500 mb-3">
                                Calculating fare...
                            </p>
                        )}

                        {fare !== null && !fareLoading && (
                            <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                                <p className="text-sm text-gray-600">
                                    Estimated Fare:
                                </p>
                                <p className="text-xl font-bold text-green-700">
                                    ₹{fare}
                                </p>
                            </div>
                        )}

                        {/* SUBMIT */}
                        <button
                            type="submit"
                            disabled={loading || fare === null}
                            className="w-full bg-black text-white py-3 rounded-lg font-medium hover:bg-gray-800 transition disabled:opacity-50"
                        >
                            {loading ? "Submitting..." : "Submit Request"}
                        </button>

                    </form>
                </div>
            </div>
        </div>
    );
}
