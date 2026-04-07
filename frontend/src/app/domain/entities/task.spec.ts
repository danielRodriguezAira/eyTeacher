import {Task} from './task';
import {Solution} from './solution';

describe('Task', () => {
    const mockStudent = {id: 'student-1', firstName: 'Ana', lastName: 'Pérez'};

    it('creates an instance with required fields', () => {
        const task = new Task(1, 'Resuelve la ecuación', 10);

        expect(task.id).toBe(1);
        expect(task.description).toBe('Resuelve la ecuación');
        expect(task.topicId).toBe(10);
    });

    it('defaults solutionList to an empty array when not provided', () => {
        const task = new Task(1, 'Descripción', 5);

        expect(task.solutionList).toEqual([]);
    });

    it('accepts a provided solutionList', () => {
        const taskStub = new Task(1, 'stub', 1);
        const solutions: Solution[] = [new Solution(7, 'Mi solución', mockStudent, taskStub)];
        const task = new Task(2, 'Calcula la derivada', 3, solutions);

        expect(task.solutionList).toHaveLength(1);
        expect(task.solutionList[0].description).toBe('Mi solución');
    });

    it('accepts null as id', () => {
        const task = new Task(null, 'Nueva tarea', 8);

        expect(task.id).toBeNull();
    });

    it('createdAt is undefined when not provided', () => {
        const task = new Task(1, 'Tarea', 1);

        expect(task.createdAt).toBeUndefined();
    });

    it('accepts createdAt when provided', () => {
        const task = new Task(1, 'Tarea', 1, [], '2026-01-15T10:00:00Z');

        expect(task.createdAt).toBe('2026-01-15T10:00:00Z');
    });

    it('allows solutionList to be mutated', () => {
        const task = new Task(1, 'Tarea', 1);
        task.solutionList.push(new Solution(1, 'Sol', mockStudent, task));

        expect(task.solutionList).toHaveLength(1);
    });
});
