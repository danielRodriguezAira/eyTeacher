export interface StudentTaskSolutionCorrection {
    id: number;
    description: string;
    teacher: {
        id: string;
        firstName: string;
        lastName: string;
    };
    solutionId: number;
}

export interface StudentTaskSolution {
    id: number;
    description: string;
    student: {
        id: string;
        email: string;
        firstName: string;
        lastName: string;
    };
    correction: StudentTaskSolutionCorrection | null;
}

export interface StudentTaskItem {
    id: number;
    description: string;
    solution: StudentTaskSolution | null;
}

export interface TopicTaskGroup {
    id: number;
    name: string;
    tasks: StudentTaskItem[];
}

export interface CategoryTaskGroup {
    id: number;
    name: string;
    topics: TopicTaskGroup[];
}

export type TaskStatus = 'WITHOUT_SOLUTION' | 'WITHOUT_CORRECTION' | 'CORRECTED';

export interface StudentTasksResponse {
    status: TaskStatus;
    categories: CategoryTaskGroup[];
}
