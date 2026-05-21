import api from "../api/axiosConfig";

export const getAllRides = async () => {
    return await api.get("/rides");
};

export const searchRides = async (source, destination) => {
    return await api.get(`/rides/search?source=${source}&destination=${destination}`);
};

export const startRide = async (rideId) => {
    return await api.put(`/rides/${rideId}/start`);
};

export const completeRide = async (rideId) => {
    return await api.put(`/rides/${rideId}/complete`);
};

export const cancelRide = async (rideId) => {
    return await api.put(`/rides/${rideId}/cancel`);
};
