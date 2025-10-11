-- Create the join table for activity registrations
CREATE TABLE IF NOT EXISTS activity_registrations (
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (activity_id, user_id),
    FOREIGN KEY (activity_id) REFERENCES Activity(ActivityId) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(UserId) ON DELETE CASCADE
);

-- Create index for better query performance
CREATE INDEX idx_activity_registrations_activity ON activity_registrations(activity_id);
CREATE INDEX idx_activity_registrations_user ON activity_registrations(user_id);
