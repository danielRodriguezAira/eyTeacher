import {AuthUser} from './auth-user';

export interface NotificationOwner extends Pick<AuthUser, 'id' | 'firstName' | 'lastName'> {}

export type NotificationEntityType = 'TASK' | 'SOLUTION' | 'CORRECTION' | 'TOPIC';

export interface Notification {
    id: number;
    message: string;
    owner: NotificationOwner;
    entityType: NotificationEntityType;
    entityId: number;
    read: boolean;
}
