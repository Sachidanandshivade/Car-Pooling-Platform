import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { getPendingRequests, acceptRideRequest } from "../services/rideRequestService";

export default function PendingRequests() {
    const navigate = useNavigate();
    const [source, setSource] = useState("");
    const [requests, setRequests] = useState([]);
    const [searched, setSearched] = useState(false);
    const [loading, setLoading] = useState(false);
    const [accepting, setAccepting] = useState(null);
    const [error, setError] = useState("");

    const handleSearch = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        setSearched(false);
        try {
            const res = await getPendingRequests(source);
            setRequests(res.data.data || []);
            setSearched(true);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to fetch requests.");
        } finally {
            setLoading(false);
        }
    };

    const handleAccept = async (requestId) => {
        setAccepting(requestId);
        try {
            const res = await acceptRideRequest(requestId);
            alert(res.data.message || "Ride accepted!");
            // Remove accepted request from list
            setRequests((prev) => prev.filter((r) => r.id !== requestId));
        } catch (err) {
            alert(err.response?.data?.message || "Failed to accept request.");
        } finally {
            setAccepting(null);
        }
    };

    const formatTime = (timeStr) => {
        if (!timeStr) return "—";
        return new Date(timeStr).toLocaleString("en-IN", {
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

            <div className="max-w-3xl mx-auto p-6 mt-6">
                <h1 className="text-2xl font-bold text-gray-800 mb-1">Pending Ride Requests</h1>
                <p className="text-gray-500 text-sm mb-6">Search by pickup location to find passengers near you.</p>

                {/* Search */}
                <form onSubmit={handleSearch} className="flex gap-3 mb-6">
                    <input
                        type="text"
                        placeholder="Enter pickup area (e.g. Koramangala)"
                        value={source}
                        onChange={(e) => setSource(e.target.value)}
                        required
                        className="flex-1 border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black text-sm"
                    />
                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-black text-white px-6 py-3 rounded-lg text-sm font-medium hover:bg-gray-800 transition disabled:opacity-50"
                    >
                        {loading ? "Searching..." : "Search"}
                    </button>
                </form>

                {error && (
                    <div className="bg-red-50 border border-red-300 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm">
                        {error}
                    </div>
                )}

                {/* Results */}
                {searched && (
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
                        <div className="p-5 border-b border-gray-100 flex items-center justify-between">
                            <h2 className="font-semibold text-gray-800">
                                Results for "{source}"
                            </h2>
                            <span className="text-sm text-gray-400">{requests.length} request(s)</span>
                        </div>

                        {requests.length === 0 ? (
                            <div className="p-8 text-center text-gray-400">
                                No pending requests found for this area.
                            </div>
                        ) : (
                            <div className="divide-y divide-gray-50">
                                {requests.map((req) => (
                                    <div key={req.id} className="p-5 flex items-start justify-between gap-4">
                                        <div className="flex-1">
                                            <div className="flex items-center gap-2 mb-2">
                                                <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-0.5 rounded-full font-semibold">
                                                    PENDING
                                                </span>
                                                <span className="text-xs text-gray-400">#{req.id}</span>
                                            </div>
                                            <div className="text-sm font-medium text-gray-800 mb-1">
                                                📍 {req.source} → {req.destination}
                                            </div>
                                            <div className="text-xs text-gray-500">
                                                🕐 {formatTime(req.requestTime)}
                                            </div>
                                            {req.passenger && (
                                                <div className="text-xs text-gray-500 mt-1">
                                                    👤 {req.passenger.name}
                                                </div>
                                            )}
                                        </div>
                                        <button
                                            onClick={() => handleAccept(req.id)}
                                            disabled={accepting === req.id}
                                            className="bg-black text-white text-sm px-5 py-2 rounded-lg hover:bg-gray-800 transition disabled:opacity-50 whitespace-nowrap"
                                        >
                                            {accepting === req.id ? "Accepting..." : "Accept"}
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
