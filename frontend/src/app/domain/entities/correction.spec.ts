import {Correction} from './correction';

describe('Correction', () => {
    it('can be created with required fields', () => {
        const correction: Correction = {
            description: 'Muy bien resuelto',
            solutionId: 42,
        };

        expect(correction.description).toBe('Muy bien resuelto');
        expect(correction.solutionId).toBe(42);
    });

    it('createdAt is optional and can be omitted', () => {
        const correction: Correction = {
            description: 'Correcto',
            solutionId: 1,
        };

        expect(correction.createdAt).toBeUndefined();
    });

    it('accepts createdAt when provided', () => {
        const correction: Correction = {
            description: 'Bien',
            solutionId: 5,
            createdAt: '2026-03-10T15:00:00Z',
        };

        expect(correction.createdAt).toBe('2026-03-10T15:00:00Z');
    });
});
