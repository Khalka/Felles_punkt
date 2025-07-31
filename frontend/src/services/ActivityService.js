// src/services/ActivityService.js
import api from './api'; // axios instance med auth-token

const endpoint = '/api/activities';

export default {
  getMyActivities() {
    return api.get(`${endpoint}/mine`);
  },

  createActivity(activity) {
    return api.post(endpoint, activity);
  },

  updateActivity(id, activity) {
    return api.put(`${endpoint}/${id}`, activity);
  },

  deleteActivity(id) {
    return api.delete(`${endpoint}/${id}`);
  },
};
