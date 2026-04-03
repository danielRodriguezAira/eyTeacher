import {Solution} from "./solution";

export class Task {
    constructor(
        public id: number | null,
        public description: string,
        public topicId: number,
        public solutionList: Solution[] = [],
        public createdAt?: string
    ) {
    }
}
