import {Category} from './category';
import {Topic} from './topic';

describe('Category', () => {
    it('creates an instance with all required fields', () => {
        const category = new Category(1, 'Matemáticas', 'Descripción de matemáticas');

        expect(category.id).toBe(1);
        expect(category.name).toBe('Matemáticas');
        expect(category.description).toBe('Descripción de matemáticas');
    });

    it('defaults topicList to an empty array when not provided', () => {
        const category = new Category(1, 'Ciencias', 'Descripción');

        expect(category.topicList).toEqual([]);
    });

    it('accepts a provided topicList', () => {
        const topics: Topic[] = [new Topic(10, 'Álgebra', 'Álgebra básica', 1)];
        const category = new Category(2, 'Matemáticas', 'Desc', topics);

        expect(category.topicList).toHaveLength(1);
        expect(category.topicList[0].name).toBe('Álgebra');
    });

    it('accepts null as id', () => {
        const category = new Category(null, 'Nueva', 'Sin ID aún');

        expect(category.id).toBeNull();
    });

    it('allows topicList to be mutated', () => {
        const category = new Category(1, 'Arte', 'Desc');
        category.topicList.push(new Topic(5, 'Pintura', 'Desc pintura', 1));

        expect(category.topicList).toHaveLength(1);
    });
});
