import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { getAllRides } from "../services/rideService";
import api from "../api/axiosConfig";

function parseJwt(token) {
    try { return JSON.parse(atob(token.split(".")[1])); }
    catch { return null; }
}

export default function Dashboard() {
    const navigate = useNavigate();
    const [rides, setRides] = useState([]);
    const [myRequests, setMyRequests] = useState([]);
    const [role, setRole] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) { navigate("/"); return; }
        const payload = parseJwt(token);
        if (payload) {
            setUserEmail(payload.sub);
            setRole(payload.role || "");
            fetchData(payload.role);
        }
    }, []);

    const fetchData = async (userRole) => {
        setLoading(true);
        try {
            const ridesRes = await getAllRides();
            setRides(ridesRes.data.data || []);

            if (userRole === "PASSENGER") {
                const reqRes = await api.get("/requests/my");
                setMyRequests(reqRes.data.data || []);
            }
        } catch (err) {
            console.log(err);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/");
    };

    const statusColor = (status) => {
        switch (status) {
            case "ACCEPTED":  return "bg-blue-100 text-blue-700";
            case "STARTED":   return "bg-yellow-100 text-yellow-700";
            case "COMPLETED": return "bg-green-100 text-green-700";
            case "CANCELLED": return "bg-red-100 text-red-700";
            case "PENDING":   return "bg-orange-100 text-orange-700";
            default:          return "bg-gray-100 text-gray-600";
        }
    };

    const formatTime = (dt) => {
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
                <div className="flex items-center gap-4">
                    <span className="text-sm text-gray-300">{userEmail}</span>
                    <span className="text-xs bg-white text-black px-2 py-1 rounded font-semibold">{role}</span>
                    <button onClick={handleLogout}
                            className="bg-white text-black text-sm px-4 py-1.5 rounded hover:bg-gray-200 transition">
                        Logout
                    </button>
                </div>
            </nav>

            <div className="max-w-5xl mx-auto p-6">
                <h1 className="text-2xl font-bold text-gray-800 mb-2">Dashboard</h1>
                <p className="text-gray-500 mb-6">Welcome back!</p>

                {/* Action Buttons */}
                <div className="flex flex-wrap gap-3 mb-8">
                    {role === "PASSENGER" && (
                        <Link to="/create-request"
                              className="bg-black text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-800 transition">
                            + Request a Ride
                        </Link>
                    )}
                    {role === "DRIVER" && (
                        <Link to="/pending-requests"
                              className="bg-black text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-800 transition">
                            View Pending Requests
                        </Link>
                    )}
                    <Link to="/my-rides"
                          className="border border-gray-300 text-gray-700 px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-100 transition">
                        My Rides
                    </Link>
                </div>

                {/* PASSENGER: Show their ride requests */}
                {role === "PASSENGER" && (
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 mb-6">
                        <div className="p-5 border-b border-gray-100 flex items-center justify-between">
                            <h2 className="text-lg font-semibold text-gray-800">My Ride Requests</h2>
                            <span className="text-sm text-gray-400">{myRequests.length} request(s)</span>
                        </div>

                        {loading ? (
                            <div className="p-8 text-center text-gray-400">Loading...</div>
                        ) : myRequests.length === 0 ? (
                            <div className="p-8 text-center text-gray-400">
                                No ride requests yet.{" "}
                                <Link to="/create-request" className="text-black underline">Request one now</Link>
                            </div>
                        ) : (
                            <div className="divide-y divide-gray-50">
                                {myRequests.map((req) => (
                                    <div key={req.id} className="px-5 py-4 flex items-center justify-between">
                                        <div>
                                            <p className="text-sm font-medium text-gray-800">
                                                📍 {req.source} → {req.destination}
                                            </p>
                                            <p className="text-xs text-gray-500 mt-0.5">
                                                🕐 {formatTime(req.requestTime)}
                                            </p>
                                            {req.driver && (
                                                <p className="text-xs text-gray-500 mt-0.5">
                                                    🧑‍✈️ Driver: {req.driver.name}
                                                </p>
                                            )}
                                        </div>
                                        <span className={`text-xs px-2 py-1 rounded-full font-semibold ${statusColor(req.status)}`}>
                                            {req.status}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* All Rides */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
                    <div className="p-5 border-b border-gray-100">
                        <h2 className="text-lg font-semibold text-gray-800">All Rides</h2>
                    </div>

                    {loading ? (
                        <div className="p-8 text-center text-gray-400">Loading rides...</div>
                    ) : rides.length === 0 ? (
                        <div className="p-8 text-center text-gray-400">
                            No rides yet — rides appear here once a driver accepts a request.
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm">
                                <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
                                <tr>
                                    <th className="px-5 py-3 text-left">ID</th>
                                    <th className="px-5 py-3 text-left">From</th>
                                    <th className="px-5 py-3 text-left">To</th>
                                    <th className="px-5 py-3 text-left">Fare (₹)</th>
                                    <th className="px-5 py-3 text-left">Status</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-50">
                                {rides.map((ride) => (
                                    <tr key={ride.id} className="hover:bg-gray-50">
                                        <td className="px-5 py-3 text-gray-500">#{ride.id}</td>
                                        <td className="px-5 py-3 font-medium text-gray-800">{ride.source}</td>
                                        <td className="px-5 py-3 text-gray-600">{ride.destination}</td>
                                        <td className="px-5 py-3 text-gray-800">₹{ride.fare}</td>
                                        <td className="px-5 py-3">
                                                <span className={`px-2 py-1 rounded-full text-xs font-semibold ${statusColor(ride.status)}`}>
                                                    {ride.status}
                                                </span>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}