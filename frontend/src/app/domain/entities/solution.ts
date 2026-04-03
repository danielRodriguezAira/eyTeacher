import {AuthUser} from './auth-user';
import {Task} from "./task";
import {Correction} from "./correction";

export interface Student extends Pick<AuthUser, 'id' | 'firstName' | 'lastName'> {}

export class Solution {
    constructor(
        public id: number | null,
        public description: string,
        public student: Student,
        public task: Task,
        public correction?: Correction,
        public createdAt?: string
    ) {
    }
}
