package com.multithreads.management;

import com.multithreads.management.workers.FileProvider;
import com.multithreads.management.workers.FileService;
import com.sun.corba.se.impl.orbutil.closure.Future;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

@RunWith(MockitoJUnitRunner.class)
public class CommunicatorTest {

    @Mock
    private FileService fileService;

  /*  private FileProvider fileProvider = new FileProvider(Logger.getRootLogger());*/

    @Mock
    private ResourceBundle resourceBundle;

    @InjectMocks
    private Communicator communicator;

    @Before
    public  void  setUp() throws IOException {

      /*  //MockitoAnnotations.initMocks(communicator);
        when(fileService.createTaskFuture(null, 0, 0, 0, null)).thenReturn(null);
        when(fileProvider.getDirectory("")).thenReturn(null);
        when(fileProvider.getFile("")).thenReturn(null);*/
    }

    @Test
    public void openConsoleTest(){
       communicator.openConsole();
       verify(fileService).createTaskFuture(null, 0, 0, 0, null);
    }
}
