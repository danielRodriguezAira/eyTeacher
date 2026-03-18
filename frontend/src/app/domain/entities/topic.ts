import {Task} from './task';

export class Topic {
    constructor(
        public id: number | null,
        public name: string,
        public description: string,
        public categoryId: number,
        public taskList: Task[] = []
    ) {
    }
}
