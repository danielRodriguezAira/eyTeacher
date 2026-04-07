import {
    CategoryTaskGroup,
    StudentTaskItem,
    StudentTaskSolution,
    StudentTaskSolutionCorrection,
    StudentTasksResponse,
    TaskStatus,
    TopicTaskGroup,
} from './student-tasks-response';

describe('TaskStatus', () => {
    it('contains all valid status values', () => {
        const statuses: TaskStatus[] = ['WITHOUT_SOLUTION', 'WITHOUT_CORRECTION', 'CORRECTED'];

        expect(statuses).toContain('WITHOUT_SOLUTION');
        expect(statuses).toContain('WITHOUT_CORRECTION');
        expect(statuses).toContain('CORRECTED');
    });
});

describe('StudentTaskSolutionCorrection', () => {
    it('can be created with all fields', () => {
        const correction: StudentTaskSolutionCorrection = {
            id: 1,
            description: 'Correcto',
            teacher: {id: 't-1', firstName: 'Prof', lastName: 'García'},
            solutionId: 10,
            createdAt: '2026-03-01',
        };

        expect(correction.id).toBe(1);
        expect(correction.description).toBe('Correcto');
        expect(correction.teacher.id).toBe('t-1');
        expect(correction.solutionId).toBe(10);
        expect(correction.createdAt).toBe('2026-03-01');
    });

    it('createdAt is optional', () => {
        const correction: StudentTaskSolutionCorrection = {
            id: 2,
            description: 'Bien',
            teacher: {id: 't-2', firstName: 'Ana', lastName: 'López'},
            solutionId: 5,
        };

        expect(correction.createdAt).toBeUndefined();
    });
});

describe('StudentTaskSolution', () => {
    it('can be created with correction', () => {
        const correction: StudentTaskSolutionCorrection = {
            id: 1,
            description: 'OK',
            teacher: {id: 't-1', firstName: 'Prof', lastName: 'A'},
            solutionId: 1,
        };
        const solution: StudentTaskSolution = {
            id: 1,
            description: 'Mi solución',
            student: {id: 's-1', email: 'a@b.com', firstName: 'Luis', lastName: 'P'},
            correction,
            createdAt: '2026-02-01',
        };

        expect(solution.id).toBe(1);
        expect(solution.correction).toBe(correction);
        expect(solution.student.email).toBe('a@b.com');
    });

    it('correction can be null', () => {
        const solution: StudentTaskSolution = {
            id: 2,
            description: 'Sin corrección aún',
            student: {id: 's-2', email: 'b@b.com', firstName: 'Eva', lastName: 'R'},
            correction: null,
        };

        expect(solution.correction).toBeNull();
    });

    it('createdAt is optional', () => {
        const solution: StudentTaskSolution = {
            id: 3,
            description: 'Solución',
            student: {id: 's-3', email: 'c@b.com', firstName: 'Marta', lastName: 'S'},
            correction: null,
        };

        expect(solution.createdAt).toBeUndefined();
    });
});

describe('StudentTaskItem', () => {
    it('can be created with a solution', () => {
        const solution: StudentTaskSolution = {
            id: 1,
            description: 'Solución',
            student: {id: 's-1', email: 'x@x.com', firstName: 'A', lastName: 'B'},
            correction: null,
        };
        const item: StudentTaskItem = {
            id: 10,
            description: 'Tarea de álgebra',
            solution,
            createdAt: '2026-01-01',
        };

        expect(item.id).toBe(10);
        expect(item.solution).toBe(solution);
        expect(item.createdAt).toBe('2026-01-01');
    });

    it('solution can be null', () => {
        const item: StudentTaskItem = {
            id: 11,
            description: 'Tarea sin resolver',
            solution: null,
        };

        expect(item.solution).toBeNull();
    });

    it('createdAt is optional', () => {
        const item: StudentTaskItem = {id: 12, description: 'Tarea', solution: null};

        expect(item.createdAt).toBeUndefined();
    });
});

describe('TopicTaskGroup', () => {
    it('can be created with tasks', () => {
        const group: TopicTaskGroup = {
            id: 1,
            name: 'Álgebra',
            tasks: [{id: 5, description: 'Tarea 1', solution: null}],
        };

        expect(group.id).toBe(1);
        expect(group.name).toBe('Álgebra');
        expect(group.tasks).toHaveLength(1);
    });

    it('can have an empty tasks array', () => {
        const group: TopicTaskGroup = {id: 2, name: 'Vacío', tasks: []};

        expect(group.tasks).toEqual([]);
    });
});

describe('CategoryTaskGroup', () => {
    it('can be created with topics', () => {
        const group: CategoryTaskGroup = {
            id: 1,
            name: 'Matemáticas',
            topics: [{id: 10, name: 'Álgebra', tasks: []}],
        };

        expect(group.id).toBe(1);
        expect(group.name).toBe('Matemáticas');
        expect(group.topics).toHaveLength(1);
    });

    it('can have multiple topics', () => {
        const group: CategoryTaskGroup = {
            id: 2,
            name: 'Ciencias',
            topics: [
                {id: 20, name: 'Física', tasks: []},
                {id: 21, name: 'Química', tasks: []},
            ],
        };

        expect(group.topics).toHaveLength(2);
    });
});

describe('StudentTasksResponse', () => {
    it('can be created for WITHOUT_SOLUTION status', () => {
        const response: StudentTasksResponse = {
            status: 'WITHOUT_SOLUTION',
            categories: [],
        };

        expect(response.status).toBe('WITHOUT_SOLUTION');
        expect(response.categories).toEqual([]);
    });

    it('can be created for WITHOUT_CORRECTION status', () => {
        const response: StudentTasksResponse = {
            status: 'WITHOUT_CORRECTION',
            categories: [],
        };

        expect(response.status).toBe('WITHOUT_CORRECTION');
    });

    it('can be created for CORRECTED status', () => {
        const response: StudentTasksResponse = {
            status: 'CORRECTED',
            categories: [{id: 1, name: 'Matemáticas', topics: []}],
        };

        expect(response.status).toBe('CORRECTED');
        expect(response.categories).toHaveLength(1);
    });

    it('contains full nested structure', () => {
        const response: StudentTasksResponse = {
            status: 'WITHOUT_CORRECTION',
            categories: [
                {
                    id: 1,
                    name: 'Matemáticas',
                    topics: [
                        {
                            id: 10,
                            name: 'Álgebra',
                            tasks: [
                                {
                                    id: 100,
                                    description: 'Ecuaciones',
                                    solution: {
                                        id: 1000,
                                        description: 'Mi respuesta',
                                        student: {id: 's-1', email: 'a@a.com', firstName: 'Ana', lastName: 'P'},
                                        correction: null,
                                    },
                                },
                            ],
                        },
                    ],
                },
            ],
        };

        expect(response.categories[0].topics[0].tasks[0].id).toBe(100);
        expect(response.categories[0].topics[0].tasks[0].solution?.id).toBe(1000);
    });
});
