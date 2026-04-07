import {Topic} from './topic';
import {Task} from './task';

describe('Topic', () => {
    it('creates an instance with all required fields', () => {
        const topic = new Topic(1, 'Álgebra', 'Álgebra lineal', 42);

        expect(topic.id).toBe(1);
        expect(topic.name).toBe('Álgebra');
        expect(topic.description).toBe('Álgebra lineal');
        expect(topic.categoryId).toBe(42);
    });

    it('defaults taskList to an empty array when not provided', () => {
        const topic = new Topic(1, 'Geometría', 'Geometría plana', 10);

        expect(topic.taskList).toEqual([]);
    });

    it('accepts a provided taskList', () => {
        const tasks: Task[] = [new Task(5, 'Ejercicio 1', 1)];
        const topic = new Topic(2, 'Trigonometría', 'Funciones trigonométricas', 10, tasks);

        expect(topic.taskList).toHaveLength(1);
        expect(topic.taskList[0].description).toBe('Ejercicio 1');
    });

    it('accepts null as id', () => {
        const topic = new Topic(null, 'Nuevo tema', 'Descripción', 5);

        expect(topic.id).toBeNull();
    });

    it('allows taskList to be mutated', () => {
        const topic = new Topic(1, 'Cálculo', 'Desc', 1);
        topic.taskList.push(new Task(1, 'Tarea A', 1));

        expect(topic.taskList).toHaveLength(1);
    });
});
