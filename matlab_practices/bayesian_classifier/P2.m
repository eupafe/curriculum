%% NAME AND SURNAME OF THE MEMBERS OF THE GROUP:
%MEMBER 1: Valèria Ezquerra Rodriguez
%MEMBER 2: Laia Casas Irure
%MEMBER 3: Eugènia Pacheco Ferrando

f = fopen('smsspamcollection/SMSSpamCollection', 'r');                      % Open the file

all = textscan(f, '%s %s', 'delimiter', '\n\t');                            % Read the file separating class and text
fclose(f);                                                                  % Close the file

all = preparaDades(all);                                                    % Clean the data and separate the text by spaces

[train, test] = separaTrainTest(all, 0.8);                                  % Separate the data in train/test subsets

[ham, spam] = separaHamSpam(train);                                         % Separate the training data by class

[hamBag, hamCountTotal] = generaBagOfWords(ham);                            % Generate Bags of Words and total counts
[spamBag, spamCountTotal] = generaBagOfWords(spam);

%[hamBag, spamBag, test] = crossDeleteWords(hamBag, spamBag, test);         % This function filters out repeated words between classes to improve the accuracy.
                                                                            % However, it is pretty slow, so comment it out to run faster tests

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                            %
%  Write your code below this point			 %
%                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Question 1 code

count_ham = 0;
count_spam = 0;

for i = 1: size(all, 1)
    string_message = all(i,1);

    if strcmp(string_message, 'ham')
        count_ham = count_ham + 1;
    else 
        count_spam = count_spam + 1;
    end 

end

HamKeys = hamBag.keys;
SpamKeys = spamBag.keys;

correct_predictions = 0;

for j=1 : size(test, 1)
    fprintf(' J is %d\n', j);

    % Obtain the message %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
        % Access the content of the second column of test matrix
        column = test(j, 2);
        
        % Access the words
        primera_fila = column{1, 1};
    
        % Calculate probability depending on the words
        num_columns = size(primera_fila, 2);
        PM_C_ham = 1;
        PM_C_spam = 1;
        
        for column = 1: num_columns
            valor_actual = primera_fila(1, column);
            string = cell2mat(valor_actual);
    
            fprintf('The value is %s\n', string);
            if isempty(string)

            else
                
                % Find it in ham
                if ismember(string, hamBag.keys) == true
                    number = hamBag(string);
                    divHam = (number/hamCountTotal);
                   
                else 
                    % If word is not found, probability is considered 0
                    divHam = 0;
                end
                PM_C_ham = PM_C_ham * divHam;
        
                % Find it in spam
                if ismember(string, spamBag.keys) == true
                    number = spamBag(string);
                    divSpam = (number/spamCountTotal);
                else 
                    % If word is not found, probability is considered 0
                    divSpam = 0;
                end
                PM_C_spam = PM_C_spam * divSpam;
            end
           
        end
    
    
    % Probability of ham within the training set
    PC_ham = size(ham, 1) / size(train, 1);
    Pham = PM_C_ham * PC_ham;
    
    % Probability of spam within the training set
    PC_spam = size(spam, 1) / size(train, 1);
    Pspam = PM_C_spam * PC_spam;
    
    P = horzcat(Pham, Pspam);
    
    % Obtain most probable class, the one that maximizes P(C|M)
    [~, argmax_class] = max(P);
    
    % Show message

    actual_value = train(j, 1);
    actual_string = cell2mat(actual_value);

    if argmax_class == 1
        disp('This message is ham');

        if strcmp(actual_string, 'ham')
            correct_predictions = correct_predictions + 1;
        end

    else
        disp('This message is spam');

         if strcmp(actual_string, 'spam')
            correct_predictions = correct_predictions + 1;
        end
    end

end 

accuracy = correct_predictions / size(test, 1);