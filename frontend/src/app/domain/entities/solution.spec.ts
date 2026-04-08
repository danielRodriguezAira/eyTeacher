import {Solution} from './solution';
import {Task} from './task';
import {Correction} from './correction';

describe('Solution', () => {
    const mockStudent = {id: 'student-1', firstName: 'Ana', lastName: 'López'};
    const mockTask = new Task(10, 'Ejercicio de álgebra', 5);

    it('creates an instance with required fields', () => {
        const solution = new Solution(1, 'Mi respuesta', mockStudent, mockTask);

        expect(solution.id).toBe(1);
        expect(solution.description).toBe('Mi respuesta');
        expect(solution.student).toBe(mockStudent);
        expect(solution.task).toBe(mockTask);
    });

    it('accepts null as id', () => {
        const solution = new Solution(null, 'Respuesta', mockStudent, mockTask);

        expect(solution.id).toBeNull();
    });

    it('correction is undefined when not provided', () => {
        const solution = new Solution(1, 'Respuesta', mockStudent, mockTask);

        expect(solution.correction).toBeUndefined();
    });

    it('accepts a correction when provided', () => {
        const correction: Correction = {
            description: 'Bien hecho',
            solutionId: 1,
            createdAt: '2026-03-01T09:00:00Z',
        };
        const solution = new Solution(1, 'Respuesta', mockStudent, mockTask, correction);

        expect(solution.correction).toEqual(correction);
        expect(solution.correction!.description).toBe('Bien hecho');
    });

    it('createdAt is undefined when not provided', () => {
        const solution = new Solution(1, 'Respuesta', mockStudent, mockTask);

        expect(solution.createdAt).toBeUndefined();
    });

    it('accepts createdAt when provided', () => {
        const solution = new Solution(1, 'Respuesta', mockStudent, mockTask, undefined, '2026-02-20T08:30:00Z');

        expect(solution.createdAt).toBe('2026-02-20T08:30:00Z');
    });

    it('accepts both correction and createdAt', () => {
        const correction: Correction = {description: 'Correcto', solutionId: 2};
        const solution = new Solution(2, 'Respuesta completa', mockStudent, mockTask, correction, '2026-04-01T12:00:00Z');

        expect(solution.correction).toBeDefined();
        expect(solution.createdAt).toBe('2026-04-01T12:00:00Z');
    });
});
