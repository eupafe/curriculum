function [x_final, y_final, n_iteracions, x_real, y_real, minim_final, minim_real] = Gradient_Descendent (tria_funcio, x_inicial, y_inicial, alfa)

%Initialization of variables (you can change the first two variables)

max_iteration = 10000;
tolerance = 0.01;
centered_x = 0;
centered_y = 0;

positions = [];
iterator = 0;

%Generate function

z = Genera_fxy (tria_funcio);

x_max = (size(z,1)-1)/2;
x_min = -(size(z,1)-1)/2;

y_max = (size(z,2)-1)/2;
y_min = -(size(z,2)-1)/2;

%Since we have seen that the center of the matrix is not (0,0) then we have
%to center everything. As we know that the center is (100,100) then we have
%to add 100 to whatever value they give us.


    if (x_inicial > 100)
        centered_x = x_inicial - 100;
    else
        centered_x = x_inicial + 100;
    end
    
    if (y_inicial > 100)
        centered_y = y_inicial - 100;
    else
        centered_y = y_inicial + 100;
    end

%if ( (x_inicial <= (x_max - 1)) &&  ((x_min + 1) <= x_inicial) )
    %centered_x = x_inicial + 100;
%end

%if ( (y_inicial <= (y_max - 1)) && ((y_min + 1) <= y_inicial) )
    %centered_y = y_inicial + 100;
    
%end
 
 
for i = 1: (max_iteration + 1)

   
   xo = z(round(centered_x)-1, round(centered_y));
   xf = z(round(centered_x)+1, round(centered_y));
 
   yo = z(round(centered_x), round(centered_y)-1);
   yf = z(round(centered_x), round(centered_y)+1);

   %Gradient of x
   difference_x = xf - xo;
   gradient_x = difference_x / 2;

   %Gradient of y
   difference_y = yf - yo;
   gradient_y = difference_y / 2;
   
   centered_x = centered_x - (alfa * gradient_x);
   centered_y = centered_y - (alfa * gradient_y);
          
   real_tolerance = (abs(alfa * gradient_x) + abs(alfa * gradient_y));

   %The algorithm has reached the minimum (tolerance)
   if real_tolerance < tolerance
      break;
   end

   %The algorithm has reached the minimum (max_iteration)
   if i == (max_iteration + 1)
      break;
   end
   
   %Fill the variables with its corresponding values
   positions = [positions, [centered_x-100; centered_y-100]];
   
   iterator = iterator + 1;

end


x_final = positions(1, iterator);
y_final = positions(2, iterator);

n_iteracions = iterator;

minim_final = (z(round(x_final) + 100, round(y_final) + 100));
[M,I] = min(min(z));
minim_real = M;
[row,column] = find(z == M);

%Since we added 100 at the beginning, to be able to know what is the real 
 % X and Y positions we have to substract the exact amount

x_real = row - 100;
y_real = column - 100;




end