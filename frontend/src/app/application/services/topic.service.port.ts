import {Observable} from 'rxjs';
import {Topic} from '../../domain/entities/topic';

export interface TopicServicePort {
    getTopicById(id: number): Observable<Topic>;
    saveTopic(topic: Topic): Observable<any>;
    deleteTopic(id: number): Observable<void>;
    subscribeStudents(topicId: number, ownerId: string, studentEmailList: string[]): Observable<any>;
}
