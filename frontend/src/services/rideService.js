import api from "../utils/axiosConfig";

// Create Ride
export const createRide = async (rideData) => {

    return await api.post("/rides", rideData);
};