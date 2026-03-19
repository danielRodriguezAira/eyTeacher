import {AuthUser} from './auth-user';

export interface NotificationOwner extends Pick<AuthUser, 'id' | 'firstName' | 'lastName'> {}

export interface Notification {
    id: number;
    message: string;
    owner: NotificationOwner;
    goTo: string;
    read: boolean;
}
