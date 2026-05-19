import { useState } from "react";
import { createRide } from "../services/rideService";

export default function CreateRide() {

    const [rideData, setRideData] = useState({
        source: "",
        destination: "",
        departureTime: "",
        fare: 0,
    });

    const handleChange = (e) => {

        setRideData({
            ...rideData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await createRide(rideData);

            alert("Ride Created Successfully");

        } catch (error) {

            alert(error.response?.data?.message || "Failed to create ride");
        }
    };

    return (

        <div className="min-h-screen flex items-center justify-center bg-gray-100">

            <form
                onSubmit={handleSubmit}
                className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md"
            >

                <h1 className="text-3xl font-bold mb-6 text-center">
                    Create Ride
                </h1>

                <input
                    type="text"
                    name="source"
                    placeholder="Source"
                    onChange={handleChange}
                    className="w-full border p-3 rounded mb-4"
                />

                <input
                    type="text"
                    name="destination"
                    placeholder="Destination"
                    onChange={handleChange}
                    className="w-full border p-3 rounded mb-4"
                />

                <input
                    type="datetime-local"
                    name="departureTime"
                    onChange={handleChange}
                    className="w-full border p-3 rounded mb-4"
                />

                <input
                    type="number"
                    name="fare"
                    placeholder="Fare"
                    onChange={handleChange}
                    className="w-full border p-3 rounded mb-4"
                />

                <button
                    type="submit"
                    className="w-full bg-black text-white py-3 rounded"
                >
                    Create Ride
                </button>

            </form>

        </div>
    );
}