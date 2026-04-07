import {PageResponse} from './page-response';

describe('PageResponse', () => {
    it('can be created with a generic content type', () => {
        const response: PageResponse<string> = {
            content: ['a', 'b', 'c'],
            page: 0,
            size: 10,
            hasNext: false,
        };

        expect(response.content).toEqual(['a', 'b', 'c']);
        expect(response.page).toBe(0);
        expect(response.size).toBe(10);
        expect(response.hasNext).toBe(false);
    });

    it('hasNext is true when there are more pages', () => {
        const response: PageResponse<number> = {
            content: [1, 2, 3],
            page: 0,
            size: 3,
            hasNext: true,
        };

        expect(response.hasNext).toBe(true);
    });

    it('content can be an empty array', () => {
        const response: PageResponse<object> = {
            content: [],
            page: 5,
            size: 10,
            hasNext: false,
        };

        expect(response.content).toHaveLength(0);
        expect(response.page).toBe(5);
    });

    it('works with object content type', () => {
        const item = {id: 1, name: 'Test'};
        const response: PageResponse<{id: number; name: string}> = {
            content: [item],
            page: 2,
            size: 1,
            hasNext: false,
        };

        expect(response.content[0]).toEqual(item);
    });
});
