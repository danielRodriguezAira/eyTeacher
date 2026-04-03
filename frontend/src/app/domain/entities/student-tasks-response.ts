export interface StudentTaskSolutionCorrection {
    id: number;
    description: string;
    teacher: {
        id: string;
        firstName: string;
        lastName: string;
    };
    solutionId: number;
    createdAt?: string;
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
    createdAt?: string;
}

export interface StudentTaskItem {
    id: number;
    description: string;
    solution: StudentTaskSolution | null;
    createdAt?: string;
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
