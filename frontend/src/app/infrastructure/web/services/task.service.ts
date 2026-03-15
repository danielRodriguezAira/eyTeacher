import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Task} from '../../../domain/entities/task';
import {TaskServicePort} from '../../../application/services/task.service.port';

@Injectable({
    providedIn: 'root'
})
export class TaskService implements TaskServicePort {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/tasks';

    getTaskById(id: number): Observable<Task> {
        return this.http.get<Task>(`${this.API_URL}/${id}`);
    }

    saveTask(task: Task): Observable<any> {
        return this.http.post<any>(`${this.API_URL}`, task);
    }

    deleteTask(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}
