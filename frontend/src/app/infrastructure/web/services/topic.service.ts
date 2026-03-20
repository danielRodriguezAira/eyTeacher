import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Topic} from '../../../domain/entities/topic';
import {TopicServicePort} from '../../../application/services/topic.service.port';

@Injectable({
    providedIn: 'root'
})
export class TopicService implements TopicServicePort {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/topics';

    getTopicById(id: number): Observable<Topic> {
        return this.http.get<Topic>(`${this.API_URL}/${id}`);
    }

    saveTopic(topic: Topic): Observable<any> {
        return this.http.post<any>(`${this.API_URL}`, topic);
    }

    deleteTopic(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }

    subscribeStudents(topicId: number, ownerId: string, studentEmailList: string[]): Observable<any> {
        return this.http.post<any>(`${this.API_URL}/${topicId}/students`, {
            topicId,
            ownerId,
            studentEmailList
        });
    }
}
