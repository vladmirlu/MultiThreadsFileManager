package com.multithreads.management.workers;

import com.multithreads.management.model.FilesDTO;
import com.multithreads.management.task.FileCopyist;
import com.multithreads.statistic.StatisticService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Mock
    private ExecutorService fileWorkersPool;

    @Mock
    private StatisticService statisticService;

    @Mock
    private FileProvider fileProvider;

    @Mock
    private File file;

    @Mock
    private FileCopyist fileCopyist;

    @Mock
    private Future<File> future;

    @Mock
    private FilesDTO filesDTO;

    @InjectMocks
    FileService fileService;

    @Before
    public void setUp() throws FileNotFoundException {
        MockitoAnnotations.initMocks(this);
        when(fileWorkersPool.submit(new FileCopyist(filesDTO, statisticService), file)).thenReturn(future);
        when(statisticService.getStatisticsPool()).thenReturn(any(ExecutorService.class));
        when(fileProvider.getFile(anyString())).thenReturn(any(File.class));
        when(fileProvider.getDirectory(anyString())).thenReturn(any(File.class));
    }

    @Test
    public void createTaskFutureTest(){
        fileService.createTaskFuture(file, 0,0,0, file);
        verify(fileWorkersPool.submit(fileCopyist, file));
    }
}