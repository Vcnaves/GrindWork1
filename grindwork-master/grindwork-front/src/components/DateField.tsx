import { FaCalendarAlt } from "react-icons/fa";

interface Props{
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  value?: string;
  name?:string;
}

 const DateField = ({onChange, value, name}:Props) => {
    
  return (
    <>
      <div className="flex w-full mt-2 bg-slate-200 rounded-md ">
        <div className="flex w-full ml-2">
          <FaCalendarAlt className=" h-full mr-2 text-gray-800" />
          <input
            type="date"
            name={name}
            onChange={onChange}
            value={value}
            className="block w-full border h-12 p-2 bg-slate-200 rounded-md"
          />
        </div>
      </div>
    </>
  )
}

export default DateField;