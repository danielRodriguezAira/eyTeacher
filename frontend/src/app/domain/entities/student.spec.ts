import {Student} from './student';

describe('Student', () => {
    it('can be created with all required fields', () => {
        const student: Student = {
            id: 'stu-1',
            email: 'alumno@school.com',
            firstName: 'Laura',
            lastName: 'Fernández',
        };

        expect(student.id).toBe('stu-1');
        expect(student.email).toBe('alumno@school.com');
        expect(student.firstName).toBe('Laura');
        expect(student.lastName).toBe('Fernández');
    });

    it('accepts different student data', () => {
        const student: Student = {
            id: 'stu-99',
            email: 'otro@school.com',
            firstName: 'Miguel',
            lastName: 'Sánchez',
        };

        expect(student.id).toBe('stu-99');
        expect(student.email).toBe('otro@school.com');
    });
});
