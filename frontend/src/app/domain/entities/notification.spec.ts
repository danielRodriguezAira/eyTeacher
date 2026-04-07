import {Notification, NotificationEntityType, NotificationOwner} from './notification';

describe('NotificationOwner', () => {
    it('can be created with required fields', () => {
        const owner: NotificationOwner = {id: 'user-1', firstName: 'Carlos', lastName: 'Ruiz'};

        expect(owner.id).toBe('user-1');
        expect(owner.firstName).toBe('Carlos');
        expect(owner.lastName).toBe('Ruiz');
    });
});

describe('NotificationEntityType', () => {
    it('accepts all valid entity types', () => {
        const types: NotificationEntityType[] = ['TASK', 'SOLUTION', 'CORRECTION', 'TOPIC'];

        expect(types).toContain('TASK');
        expect(types).toContain('SOLUTION');
        expect(types).toContain('CORRECTION');
        expect(types).toContain('TOPIC');
    });
});

describe('Notification', () => {
    it('can be created with all fields', () => {
        const owner: NotificationOwner = {id: 'user-1', firstName: 'María', lastName: 'Gómez'};
        const notification: Notification = {
            id: 100,
            message: 'Nueva tarea disponible',
            owner,
            entityType: 'TASK',
            entityId: 55,
            read: false,
        };

        expect(notification.id).toBe(100);
        expect(notification.message).toBe('Nueva tarea disponible');
        expect(notification.owner).toBe(owner);
        expect(notification.entityType).toBe('TASK');
        expect(notification.entityId).toBe(55);
        expect(notification.read).toBe(false);
    });

    it('can be marked as read', () => {
        const owner: NotificationOwner = {id: 'u-2', firstName: 'Pedro', lastName: 'Martín'};
        const notification: Notification = {
            id: 1,
            message: 'Solución corregida',
            owner,
            entityType: 'CORRECTION',
            entityId: 3,
            read: true,
        };

        expect(notification.read).toBe(true);
    });

    it('supports all entity types', () => {
        const owner: NotificationOwner = {id: 'u-3', firstName: 'Eva', lastName: 'Torres'};
        const entityTypes: NotificationEntityType[] = ['TASK', 'SOLUTION', 'CORRECTION', 'TOPIC'];

        entityTypes.forEach((entityType, index) => {
            const notification: Notification = {
                id: index + 1,
                message: `Mensaje ${entityType}`,
                owner,
                entityType,
                entityId: index + 10,
                read: false,
            };
            expect(notification.entityType).toBe(entityType);
        });
    });
});
