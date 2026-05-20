import api from "../api/axiosConfig";

export const createRideRequest = async (data) => {
    return await api.post("/requests", data);
};

export const acceptRideRequest = async (requestId) => {
    return await api.post(`/requests/${requestId}/accept`);
};

export const getPendingRequests = async (source) => {
    return await api.get(`/requests/pending?source=${source}`);
};
